Feature: User lists

  Scenario: List users with a custom page size
    When I request the list of users with parameters:
      | page[size] | 5 |
    Then the response status should be 200
    And the response should contain 5 users

  Scenario Outline: List users with page size <size>
    When I request the list of users with parameters:
      | page[size] | <size> |
    Then the response status should be 200
    And the response should contain <size> users

    Examples:
      | size |
      | 1    |
      | 5    |
      | 10   |

  Scenario Outline: Reject page size <size>
    When I request the list of users with parameters:
      | page[size] | <size> |
    Then the response status should be 400

    Examples:
      | size |
      | 0    |
      | 101  |

  Scenario: List users with the default page size
    When I request the list of users
    Then the response status should be 200
    And the response should contain 25 users

  Scenario: List users with a total count
    When I request the list of users with parameters:
      | page[count] | true |
    Then the response status should be 200
    And the response meta should include a total

  Scenario: Get the next page of users
    Given I request the list of users with parameters:
      | page[size] | 5 |
    And the response should contain 5 users
    When I request the next page of users
    Then the response status should be 200
    And the response should contain 5 users
    And the response should contain none of the users from the previous page

  Scenario: Get the second page of users by page number
    Given I request the list of users with parameters:
      | page[number] | 1 |
      | page[size]   | 5 |
    And the response should contain 5 users
    When I request page 2 of users
    Then the response status should be 200
    And the response should contain 5 users
    And the response should contain none of the users from the previous page

  Scenario: Reject a request that combines a page number with a cursor
    Given I request the list of users with parameters:
      | page[size] | 5 |
    When I request the next page of users with page number 2
    Then the response status should be 400