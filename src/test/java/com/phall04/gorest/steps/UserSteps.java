package com.phall04.gorest.steps;

import com.phall04.gorest.client.UserClient;
import com.phall04.gorest.model.User;
import com.phall04.gorest.support.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.oneOf;

public class UserSteps {
    private final UserClient userClient = new UserClient();
    private User newUser;
    private String createdUserId;
    private boolean userDeleted;
    private final TestContext context;

    public UserSteps(TestContext context) {
        this.context = context;
    }

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
        context.getResponse().then().statusCode(201);
    }

    @When("I request the list of users")
    public void iRequestTheListOfUsers() {
        context.setResponse(userClient.getUsers());
    }

    @When("I create the user")
    public void iCreateTheUser() {
        context.setResponse(userClient.createUser(newUser));
        rememberCreatedUser();
    }

    private void rememberCreatedUser() {
        if (context.getResponse().statusCode() == 201) {
            createdUserId = context.getResponse().jsonPath().getString("id");
        }
    }

    @When("I request that user by ID")
    public void iRequestThatUserById() {
        context.setResponse(userClient.getUser(createdUserId));
    }

    @When("I update that user's name to {string}")
    public void iUpdateThatUsersNameTo(String name) {
        context.setResponse(userClient.updateUser(createdUserId, new User(null, name, null, null)));
    }

    @When("I update that user's status to {string}")
    public void iUpdateThatUsersStatusTo(String status) {
        context.setResponse(userClient.updateUser(createdUserId, new User(null, null, null, status)));
    }

    @When("I delete that user")
    public void iDeleteThatUser() {
        context.setResponse(userClient.deleteUser(createdUserId));
        if (context.getResponse().statusCode() == 204) {
            userDeleted = true;
        }
    }

    @Then("the response should contain at least one user")
    public void theResponseShouldContainAtLeastOneUser() {
        context.getResponse().then().body("data.size()", greaterThan(0));
    }

    @Then("the response should match the new user details")
    public void theResponseShouldMatchTheNewUserDetails() {
        context.getResponse().then()
                .body("name", equalTo(newUser.name()))
                .body("email", equalTo(newUser.email()))
                .body("status", equalTo(newUser.status()));
    }

    @Then("the response should include a user status")
    public void theResponseShouldIncludeAUserStatus() {
        context.getResponse().then().body("status", oneOf("active", "inactive"));
    }

    @Then("the response user name should be {string}")
    public void theResponseUserNameShouldBe(String name) {
        context.getResponse().then().body("name", equalTo(name));
    }

    @Then("the response user status should be {string}")
    public void theResponseUserStatusShouldBe(String status) {
        context.getResponse().then().body("status", equalTo(status));
    }

    @Then("the response user email should be unchanged")
    public void theResponseUserEmailShouldBeUnchanged() {
        context.getResponse().then().body("email", equalTo(newUser.email()));
    }

    @Then("requesting that user by ID should return {int}")
    public void requestingThatUserByIdShouldReturn(int expectedStatus) {
        userClient.getUser(createdUserId).then().statusCode(expectedStatus);
    }

    @Given("I have new user details with email {string}")
    public void iHaveNewUserDetailsWithEmail(String email) {
        newUser = new User(null, "API Test User", email, "active");
    }

    @When("I create the user without a token")
    public void iCreateTheUserWithoutAToken() {
        context.setResponse(userClient.createUserWithoutToken(newUser));
        rememberCreatedUser();
    }

    @When("I create the user with an invalid token")
    public void iCreateTheUserWithAnInvalidToken() {
        context.setResponse(userClient.createUserWithToken(newUser, "invalid-token"));
        rememberCreatedUser();
    }

    @When("I request a user that does not exist")
    public void iRequestAUserThatDoesNotExist() {
        context.setResponse(userClient.getUser(UUID.randomUUID().toString()));
    }

    @When("I create another user with the same email")
    public void iCreateAnotherUserWithTheSameEmail() {
        User duplicate = new User(null, "Duplicate User", newUser.email(), "active");
        context.setResponse(userClient.createUser(duplicate));
    }

    @When("I update a sample user's name to {string}")
    public void iUpdateASampleUsersNameTo(String name) {
        String sampleUserId = userClient.getUsers().jsonPath().getString("data[0].id");
        context.setResponse(userClient.updateUser(sampleUserId, new User(null, name, null, null)));
    }

    @When("I request the list of users with parameter {string} set to {string}")
    public void iRequestTheListOfUsersWithParameter(String name, String value) {
        context.setResponse(userClient.getUsersWithParam(name, value));
    }

    @When("I request the list of users with parameters:")
    public void iRequestTheListOfUsersWithParameters(Map<String, String> params) {
        context.setResponse(userClient.getUsers(params));
    }

    @Then("the response should contain {int} users")
    public void theResponseShouldContainUsers(int count) {
        context.getResponse().then().body("data.size()", equalTo(count));
    }

    @After
    public void deleteCreatedUser() {
        if (createdUserId != null && !userDeleted) {
            userClient.deleteUser(createdUserId).then().statusCode(204);
        }
    }
}