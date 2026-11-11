package com.phall04.gorest.steps;

import com.phall04.gorest.client.UserClient;
import com.phall04.gorest.model.User;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.oneOf;

public class UserSteps {
    private final UserClient userClient = new UserClient();
    private Response response;
    private User newUser;
    private String createdUserId;
    private boolean userDeleted;

    private static String uniqueEmail() {
        return "api-test-" + UUID.randomUUID() + "@example.com";
    }

    @Given("I have valid new user details")
    public void iHaveValidNewUserDetails() {
        newUser = new User(null, "API Test User", uniqueEmail(), "active");
    }

    @Given("I have new user details without a status")
    public void iHaveNewUserDetailsWithoutAStatus() {
        newUser = new User(null, "API Test User", uniqueEmail(), null);
    }

    @Given("I have new user details with status {string}")
    public void iHaveNewUserDetailsWithStatus(String status) {
        newUser = new User(null, "API Test User", uniqueEmail(), status);
    }

    @Given("a user exists")
    public void aUserExists() {
        iHaveValidNewUserDetails();
        iCreateTheUser();
        response.then().statusCode(201);
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

    @When("I request that user by ID")
    public void iRequestThatUserById() {
        response = userClient.getUser(createdUserId);
    }

    @When("I update that user's name to {string}")
    public void iUpdateThatUsersNameTo(String name) {
        response = userClient.updateUser(createdUserId, new User(null, name, null, null));
    }

    @When("I update that user's status to {string}")
    public void iUpdateThatUsersStatusTo(String status) {
        response = userClient.updateUser(createdUserId, new User(null, null, null, status));
    }

    @When("I delete that user")
    public void iDeleteThatUser() {
        response = userClient.deleteUser(createdUserId);
        if (response.statusCode() == 204) {
            userDeleted = true;
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

    @Then("the response should include a user status")
    public void theResponseShouldIncludeAUserStatus() {
        response.then().body("status", oneOf("active", "inactive"));
    }

    @Then("the response user name should be {string}")
    public void theResponseUserNameShouldBe(String name) {
        response.then().body("name", equalTo(name));
    }

    @Then("the response user status should be {string}")
    public void theResponseUserStatusShouldBe(String status) {
        response.then().body("status", equalTo(status));
    }

    @Then("the response user email should be unchanged")
    public void theResponseUserEmailShouldBeUnchanged() {
        response.then().body("email", equalTo(newUser.email()));
    }

    @Then("the response body should be empty")
    public void theResponseBodyShouldBeEmpty() {
        assertThat(response.asString(), emptyString());
    }

    @Then("requesting that user by ID should return {int}")
    public void requestingThatUserByIdShouldReturn(int expectedStatus) {
        userClient.getUser(createdUserId).then().statusCode(expectedStatus);
    }

    @After
    public void deleteCreatedUser() {
        if (createdUserId != null && !userDeleted) {
            userClient.deleteUser(createdUserId).then().statusCode(204);
        }
    }
}