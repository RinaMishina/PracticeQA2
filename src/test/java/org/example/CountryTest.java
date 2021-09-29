package org.example;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.restassured.RestAssured.given;
import static org.example.DefaultUrl.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public class CountryTest extends BaseTest {
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
    public void getAllCountries() {
        this.getGivenRequest().
                when().
                get(COUNTRY).
                then().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void createNewCountry() {
        Response response = this.getGivenRequest().
                body("{\"regionName\": \"Bryansk region\"}").
                when().
                post(REGION).
                then().
                statusCode(HttpStatus.SC_CREATED).
                extract().
                response();

        int createdRegionId = response.jsonPath().get("id");

        Response response1 = this.getGivenRequest().
                body("{\n" +
                        "  \"countryName\": \"Russia123\",\n" +
                        "  \"region\": { " +
                        "  \"id\": " + createdRegionId + ",\n " +
                        "  \"regionName\": \"Bryansk region\" " +
                        "  \n }" +
                        "  \n }").
                when().
                post(COUNTRY).
                then().
                statusCode(HttpStatus.SC_CREATED).
                body("countryName", equalTo("Russia123")).
                extract().
                response();

        int createdCountryId = response1.jsonPath().get("id");

        this.getGivenRequest().
                when().
                delete(COUNTRY_ID, createdCountryId).
                then().
                statusCode(HttpStatus.SC_NO_CONTENT);

        this.getGivenRequest().
                when().
                delete(REGION_ID, createdRegionId).
                then().
                statusCode(HttpStatus.SC_NO_CONTENT);

    }
}
