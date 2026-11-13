package com.phall04.gorest.client;

import com.phall04.gorest.config.Config;
import com.phall04.gorest.model.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserClient {

    private RequestSpecification baseRequest() {
        return given()
                .baseUri(Config.baseUrl())
                .accept(ContentType.JSON);
    }

    private RequestSpecification authenticatedRequest() {
        return baseRequest()
                .header("Authorization", "Bearer " + Config.token());
    }

    public Response getUser(String id) {
        return authenticatedRequest().when().get("/users/{id}", id);
    }
    public Response getUsers() {
        return getUsers(Map.of());
    }

    public Response getUsers(Map<String, String> params) {
        return baseRequest()
                .queryParams(params)
                .when()
                .get("/users");
    }

    public Response getUsersWithParam(String name, String value) {
        return getUsers(Map.of(name, value));
    }

    public Response createUser(User user) {
        return authenticatedRequest()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users");
    }

    public Response updateUser(String id, User changes) {
        return authenticatedRequest()
                .contentType(ContentType.JSON)
                .body(changes)
                .when()
                .patch("/users/{id}", id);
    }

    public Response deleteUser(String id) {
        return authenticatedRequest().when().delete("/users/{id}", id);
    }

    public Response createUserWithoutToken(User user) {
        return baseRequest()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users");
    }

    public Response createUserWithToken(User user, String token) {
        return baseRequest()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users");
    }
}