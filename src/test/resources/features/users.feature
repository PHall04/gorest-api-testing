@smoke
Feature: Users

  Scenario: List users
    When I request the list of users
    Then the response status should be 200
    And the response should contain at least one user

  Scenario: Create a user
    Given I have valid new user details
    When I create the user
    Then the response status should be 201
    And the response should match the new user details