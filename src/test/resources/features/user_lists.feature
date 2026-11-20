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

  Scenario: Filter user list using filter[status]
      Given I request the list of users with parameters:
        | filter[status] | active |
      Then the response status should be 200
      And every returned user should have status "active"

  Scenario: Filter user list using filter[email]
    Given I request the list of users with parameters:
      | filter[email] | @example.com |
    Then the response status should be 200
    And every returned user should have an email ending "@example.com"

  @known-defect
  Scenario: Filter using field[]
      Given I request the list of users with parameters:
        | fields[users] | id,name |
      Then the response status should be 200
      And every returned user should have an id and name
      And every user should not have an email and status

  Scenario Outline: Sort users by <field> in <direction> order
    When I request the list of users with parameters:
      | sort | <sort> |
    Then the response status should be 200
    And the response should contain at least one user
    Then the users should be sorted by "<field>" in "<direction>" order

    Examples:
      | sort        | field      | direction  |
      | name        | name       | ascending  |
      | -name       | name       | descending |
      | email       | email      | ascending  |
      | -email      | email      | descending |
      | created_at  | created_at | ascending  |
      | -created_at | created_at | descending |

  Scenario: Sort users newest first by default
    When I request the list of users
    Then the response status should be 200
    Then the users should be sorted by "created_at" in "descending" order

  Scenario Outline: Reject an invalid sort of <sort>
    When I request the list of users with parameters:
      | sort | <sort> |
    Then the response status should be 400

    Examples:
      | sort       |
      | status     |
      | name,email |

  Scenario Outline: Search users for "<term>"
    When I request the list of users with parameters:
      | search | <term> |
    Then the response status should be 200
    And the response should contain at least one user
    And every returned user should have a name or email containing "<term>"

    Examples:
      | term   |
      | sharma |
      | SHARMA |
      | ShArmA |
      | shar   |

  Scenario: Search returns the same users regardless of case
    Given I request the list of users with parameters:
      | search | sharma |
    When I search the users for "SHARMA"
    Then the response status should be 200
    And the response should contain the same users as the previous search

  Scenario: Search with no matches
    When I request the list of users with parameters:
      | search | zzzz-no-match |
    Then the response status should be 200
    And the response should contain 0 users