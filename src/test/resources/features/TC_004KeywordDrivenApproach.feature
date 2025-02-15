Feature: Automate Testzen Labs Form two

  @keyword2
  Scenario: Fill out the General Information Form two


    Given User executes test steps from "General" sheet in "excelDatatwo" and record the test steps to the feature file "featurefiletwo" and the stepdefination as "Stepdefination"


Given While filling the form navigate to Testzen Labs Form to proceed with registration
When you correctly enter First Name before moving to the next field
And You should carefully enter Last Name so that it matches your official documents
And Before proceeding further make sure to enter Phone Number to receive OTP verification
Then In the form select Country from the dropdown list to specify your nationality
And To complete your application kindly upload Resume in the specified format
Then check the Male option if applicable
And generate a random number for the pin code before submission
