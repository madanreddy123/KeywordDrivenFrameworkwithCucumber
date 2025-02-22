Feature: Automate Testzen Labs Form

  @keyword
  Scenario: Fill out the General Information Form


    Given User executes test steps from "Gana" sheet in "excelData" and record the test steps to the feature file "featurefilethree" and the stepdefination as "testcase1"

Given Navigate for practicesoftwaretestingcom
When Enter Madan into Your Name
Then Enter Madan into Your Middle Name
And Enter reddy into Your Last Name
Given Navigate for TestZENcom
And Enter 089999 into Phone Number
And Enter Dublin into Your Address
And Enter Dublin into Your Address Two
And Enter Ireland into Your Address three
And Enter 1234 into Your Pin Code
And Click on profession
And Click on exp-1
And Click on Never Registered
And Pick the option Europe from the dropdown continents
