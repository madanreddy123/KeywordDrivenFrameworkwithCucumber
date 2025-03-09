Feature: Data Table example


  @Datatable
  Scenario: Data Table example

    Given I open the application "https://testzenlabs.ie/general-information-form/"

    When I enter the following details:
      | Fields             | Values  | Xpath                                    |
      | Your Name          | Madan   |        |
      | Your Middle Name   | Mohan   |  |
      | Your Last Name     | Reddy   |                                          |
      | Phone Number       | 0899999 |    //input[@placeholder = 'Phone Number']                                      |
      | Your Address       | dublin  |                                          |
      | Your Address Two   | ireland |                                          |
      | Your Address three | ireland |                                          |
      | Your Pin Code      | 1234    |                                          |

#    And select the check box as following:
#      | Fields | Xpath |
#      | Male   |  |
#
#    And select the radio button as following:
#      | Fields          | Xpath |
#      | Major(Above 18) |  |

#    Then choose the option "Europe" from the dropdown "Asia"

    Then click on the following:
      | Fields           | Xpath |
      | Male   | //input[@id='profession-0'] |
      | Major(Above 18) |  //input[@id='exp-1']|
      | Asia |  |
      | Europe |  |

    Then click on the following:
      | Fields           | Xpath |
      | Never Registered |  |