package com.phall04.gorest.client;

import com.phall04.gorest.config.Config;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserClient {
    public Response getUsers() {
        return given()
                .baseUri(Config.baseUrl())
                .accept(ContentType.JSON)
                .when()
                .get("/users");
    }
}