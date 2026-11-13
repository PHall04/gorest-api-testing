Feature: Users

  @smoke
  Scenario: List users
    When I request the list of users
    Then the response status should be 200
    And the response should contain at least one user

  Scenario: Create a user
    Given I have valid new user details
    When I create the user
    Then the response status should be 201
    And the response should match the new user details

  Scenario: Create a user without a status
    Given I have new user details without a status
    When I create the user
    Then the response status should be 201
    And the response should include a user status

  Scenario: Create an inactive user
    Given I have new user details with status "inactive"
    When I create the user
    Then the response status should be 201
    And the response should match the new user details

  Scenario: Get a user by ID
    Given a user exists
    When I request that user by ID
    Then the response status should be 200
    And the response should match the new user details

  Scenario: Update a user's name
    Given a user exists
    When I update that user's name to "Updated API Test User"
    Then the response status should be 200
    And the response user name should be "Updated API Test User"
    And the response user email should be unchanged
    And the response user status should be "active"

  Scenario: Update a user's status
    Given a user exists
    When I update that user's status to "inactive"
    Then the response status should be 200
    And the response user status should be "inactive"
    And the response user email should be unchanged

  Scenario: Delete a user
    Given a user exists
    When I delete that user
    Then the response status should be 204
    And the response body should be empty
    And requesting that user by ID should return 404

  Scenario: