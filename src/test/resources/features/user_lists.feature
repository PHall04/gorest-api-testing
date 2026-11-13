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