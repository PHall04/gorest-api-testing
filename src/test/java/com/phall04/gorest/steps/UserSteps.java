package com.phall04.gorest.steps;

import com.phall04.gorest.client.UserClient;
import com.phall04.gorest.model.User;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class UserSteps {
    private final UserClient userClient = new UserClient();
    private Response response;
    private User newUser;
    private String createdUserId;

    @Given("I have valid new user details")
    public void iHaveValidNewUserDetails() {
        String email = "api-test-" + UUID.randomUUID() + "@example.com";
        newUser = new User(null, "API Test User", email, "active");
    }

    @When("I request the list of users")
    public void iRequestTheListOfUsers() {
        response = userClient.getUsers();
    }

    @When("I create the user")
    public void iCreateTheUser() {
        response = userClient.createUser(newUser);
        if (response.statusCode() == 201) {
            createdUserId = response.jsonPath().getString("id");
        }
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        response.then().statusCode(expectedStatus);
    }

    @Then("the response should contain at least one user")
    public void theResponseShouldContainAtLeastOneUser() {
        response.then().body("data.size()", greaterThan(0));
    }

    @Then("the response should match the new user details")
    public void theResponseShouldMatchTheNewUserDetails() {
        response.then()
                .body("name", equalTo(newUser.name()))
                .body("email", equalTo(newUser.email()))
                .body("status", equalTo(newUser.status()));
    }

    @After
    public void deleteCreatedUser() {
        if (createdUserId != null) {
            userClient.deleteUser(createdUserId).then().statusCode(204);
        }
    }
}