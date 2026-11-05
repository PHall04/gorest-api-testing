@smoke
Feature: Users

  Scenario: List users
    When I request the list of users
    Then the response status should be 200
    And the response should contain at least one user