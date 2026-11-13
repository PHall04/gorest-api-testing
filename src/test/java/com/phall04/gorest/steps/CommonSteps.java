package com.phall04.gorest.steps;

import com.phall04.gorest.support.TestContext;
import io.cucumber.java.en.Then;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CommonSteps {
    private final TestContext context;

    public CommonSteps(TestContext context) {
        this.context = context;
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        context.getResponse().then().statusCode(expectedStatus);
    }

    @Then("the response body should be empty")
    public void theResponseBodyShouldBeEmpty() {
        assertThat(context.getResponse().asString(), emptyString());
    }

    @Then("the response should report an error for field {string}")
    public void theResponseShouldReportAnErrorForField(String pointer) {
        context.getResponse().then().body("errors.pointer", hasItem(pointer));
    }

    @Then("the response meta should include a total")
    public void theResponseMetaShouldIncludeATotal() {
        context.getResponse().then().body("meta.total", greaterThan(0));
    }
}