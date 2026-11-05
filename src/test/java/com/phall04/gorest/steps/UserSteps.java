package com.phall04.gorest.steps;

import io.cucumber.java.PendingException;
import com.phall04.gorest.client.UserClient;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.greaterThan;

public class UserSteps {
    private final UserClient userClient = new UserClient();
    private Response response;

    @When("I request the list of users")
    public void iRequestTheListOfUsers() {
        response = userClient.getUsers();
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        response.then().statusCode(expectedStatus);
    }

    @Then("the response should contain at least one user")
    public void theResponseShouldContainAtLeastOneUser() {
        response.then().body("size()", greaterThan(0));
    }
}
