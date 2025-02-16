Feature: Automate Testzen Labs Form

  @keyword
  Scenario: Fill out the General Information Form


    Given User executes test steps from "General" sheet in "excelData" and record the test steps to the feature file "featurefile" and the stepdefination as "testcase1"
    
    
Given  While 1 filling the form navigation for Testzen Labs Form to proceed with registration
When you correctly entered   the First Name before moving to the next field
And You should carefully entterred  the Last Name so that it matches  your official documents
And Before proceeding further make sure to entterring the Phone Number to receive OTP verification
Then In the form selection Country from the dropdown list to specify your nationality
And To complete your application kindly upload Resume in the specified format
Then checked the Male option if applicable
And generation a random number for the pin code before submission
