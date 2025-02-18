Feature: Automate Testzen Labs Form

  @keyword
  Scenario: Fill out the General Information Form


    Given User executes test steps from "Gana" sheet in "excelData" and record the test steps to the feature file "featurefilethree" and the stepdefination as "testcase1"

    Given Navigate for Tested Labs Form to proceed with registration
    When Enter Madan into Your Name
    Then Enter Mohan into Your Middle Name
    And Enter reddy into Your Last Name
    And Enter 08999999 into Phone Number
    And Enter Dublin into Your Address
    And Enter Ireland into Your Address Two
    And Enter 12344 into Your Pin Code
    And Click on profession
    And Click on exp
    And Select on AsiaEuropeAfricaAustraliaSouth America North America Antarctica
    And Click on Never Registered
    And Upload Resume in the specified format
