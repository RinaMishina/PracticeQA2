package org.example;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.example.DefaultUrl.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class DepartmentTest extends BaseTest {
    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    public static void setUpJpa() {
        entityManagerFactory = Persistence.createEntityManagerFactory("dbo");
    }

    @AfterAll
    public static void tearDownJpa() {
        entityManagerFactory.close();
    }

    private RequestSpecification getGivenRequest() {
        return given().
                baseUri(BASE_URL).
                port(PORT).
                header("Authorization", "Bearer " + bearerToken).
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    public void createNewDepartment() throws IOException {
        final EntityManager em = entityManagerFactory.createEntityManager();

        final Region region = new Region("Чебоксары");
        em.getTransaction().begin();
        em.persist(region);
        em.getTransaction().commit();

        em.getTransaction().begin();
        final Region regionSaved = em.find(Region.class, region.getId());
        final Country country = new Country("Российская Федера1", regionSaved.getId());
        em.persist(country);
        em.getTransaction().commit();

        em.getTransaction().begin();
        final Country countrySaved = em.find(Country.class, country.getId());
        final Location location = new Location("123456", "Ленина", "Чебоксары", "Чебоксары1", countrySaved.getId());
        em.persist(location);
        em.getTransaction().commit();

        em.getTransaction().begin();
        final Location locationSaved = em.find(Location.class, location.getId());
        final Department department = new Department("Тинькофф", locationSaved.getId());
        em.persist(department);
        em.getTransaction().commit();

        this.getGivenRequest().when()
                .get(DEPARTMENT_ID, department.getId())
                .then()
                .statusCode(is(SC_OK));

        em.getTransaction().begin();
        em.remove(department);
        em.remove(location);
        em.remove(country);
        em.remove(region);
        em.getTransaction().commit();

        em.close();
    }

}
