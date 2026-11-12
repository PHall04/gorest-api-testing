Feature: User errors

  Scenario: Create a user without a token
    Given I have valid new user details
    When I create the user without a token
    Then the response status should be 401

  Scenario: Create a user with an invalid token
    Given I have valid new user details
    When I create the user with an invalid token
    Then the response status should be 401

  Scenario: Get a user that does not exist
    When I request a user that does not exist
    Then the response status should be 404

  Scenario: Create a user with an invalid email
    Given I have new user details with email "not-an-email"
    When I create the user
    Then the response status should be 422
    And the response should report an error for field "/email"

  Scenario: Create a user with a duplicate email
    Given a user exists
    When I create another user with the same email
    Then the response status should be 409

  Scenario: Update a read-only sample user
    When I update a sample user's name to "Changed"
    Then the response status should be 403

  #API doc expects 400 for unknown params but returns 200. Defect Report TBD
  @known-defect
  Scenario: List users with an unknown parameter
    When I request the list of users with parameter "unknown" set to "1"
    Then the response status should be 400