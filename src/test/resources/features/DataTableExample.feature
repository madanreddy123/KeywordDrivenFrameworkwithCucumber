Feature: Data Table example


  @Datatable
  Scenario: Data Table example

    Given I open the application "https://testzenlabs.ie/general-information-form/"

    When I enter the following details:
      | Fields             | Values  | Xpath                                    |
      | Your Name          | Madan   | //input[@placeholder='Your Name']        |
      | Your Middle Name   | Mohan   | //input[@placeholder='Your Middle Name'] |
      | Your Last Name     | Reddy   |                                          |
      | Phone Number       | 0899999 |                                          |
      | Your Address       | dublin  |                                          |
      | Your Address Two   | ireland |                                          |
      | Your Address Three | ireland |                                          |
      | Your Pin Code      | 1234    |                                          |

    And select the check box as following:
      | Fields | Xpath |
      | Male   | Xpath |

    And select the radio button as following:
      | Fields          | Xpath |
      | Major(Above 18) | Xpath |

    Then choose the option "Europe" from the dropdown "Asia"

    Then click on the following:
      | Fields           | Xpath |
      | Never Registered | Xpath |
