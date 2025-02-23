Feature: Data Table example


  Scenario: Data Table example

    Given I open the application "https://testzenlabs.ie/general-information-form/"
    When I enter the following details:
      | Fields             | Values  |
      | Your Name          | Madan   |
      | Your Middle Name   | Mohan   |
      | Your Last Name     | Reddy   |
      | Phone Number       | 0899999 |
      | Your Address       | dublin  |
      | Your Address Two   | ireland |
      | Your Address Three | ireland |
      | Your Pin Code      | 1234    |
    Then click on the following:
      | Fields           |
      | Never Registered |
    And select the check box as following:
      | Fields |
      | Male   |
    And select the radio button as following:
      | Fields          |
      | Major(Above 18) |

    Then choose the option "Europe" from the dropdown "Asia"