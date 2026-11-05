package com.phall04.gorest.client;

import com.phall04.gorest.config.Config;
import com.phall04.gorest.model.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

//A class representing the UserEndpoint of GOREST
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

    public Response getUsers() {
        return baseRequest().when().get("/users");
    }

    public Response createUser(User user) {
        return authenticatedRequest()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post("/users");
    }

    public Response deleteUser(long id) {
        return authenticatedRequest()
                .when()
                .delete("/users/" + id);
    }
}