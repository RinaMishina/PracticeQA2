package org.example;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;
import static org.example.DefaultUrl.*;

public class BaseTest {
    protected String bearerToken;

    @BeforeAll
    public void authorization() {
        Response response = given().
                baseUri(BASE_URL).
                port(PORT).
                basePath(AUTHENTICATE).
                contentType(ContentType.JSON).
                body("{\n" +
                "  \"password\": \"u7ljdajLNo7PsVw7\",\n" +
                "  \"rememberMe\": true,\n" +
                "  \"username\": \"admin\" \n}").
                accept(ContentType.JSON).
                filters(new RequestLoggingFilter(), new ResponseLoggingFilter()).
                when().
                post().
                then().
                statusCode(HttpStatus.SC_OK).
                extract().
                response();

        bearerToken = response.jsonPath().get("id_token");

    }
}
