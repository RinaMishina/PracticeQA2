package org.example;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.example.DefaultUrl.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class RegionTest extends BaseTest {
    private static EntityManagerFactory entityManagerFactory;

    private String getRandomRegion() {
        return "Москва" + (int) (Math.random() * 100);
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

    @BeforeAll
    public static void setUpJpa() {
        entityManagerFactory = Persistence.createEntityManagerFactory("dbo");
    }

    @AfterAll
    public static void tearDownJpa() {
        entityManagerFactory.close();
    }

    @Test
    public void getRegion() {
        this.getGivenRequest().
                when().
                get(REGION).
                then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void createGetUpdateAndDeleteRegion() {
        String randomRegion = this.getRandomRegion();

        Response response = this.getGivenRequest().
                body("{\"regionName\": \"" + randomRegion + "\"}").
                when().
                post(REGION).
                then().
                statusCode(HttpStatus.SC_CREATED).
                extract().
                response();

        int createdId = response.jsonPath().get("id");

        this.getGivenRequest().
                when().
                get(REGION_ID, createdId).
                then().
                statusCode(HttpStatus.SC_OK).
                body("id", equalTo(createdId), "regionName", equalTo(randomRegion));

        this.getGivenRequest().
                body("{\n" +
                        "  \"id\":  " + createdId + " ,\n" +
                        "  \"regionName\": \"Kazan\" \n}").
                when().
                put(REGION_ID, createdId).
                then().
                statusCode(HttpStatus.SC_OK).
                body("id", equalTo(createdId), "regionName", equalTo("Kazan"));

        this.getGivenRequest().
                when().
                delete(REGION_ID, createdId).
                then().
                statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    public void shouldGetCountOfAllRegions() {

        final EntityManager em = entityManagerFactory.createEntityManager();

        final List<Region> regions = em.createQuery("SELECT r FROM Region r", Region.class).getResultList();
        int regionCount = regions.size();

        this.getGivenRequest().
                when().
                get(REGION).
                then().body("size()", is(regionCount)).statusCode(HttpStatus.SC_OK);

        em.close();
    }

}
