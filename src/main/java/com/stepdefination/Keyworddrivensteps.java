package com.stepdefination;

import com.base.ActionClass;
import com.base.DriverManager;
import com.pages.GeneralInformation;
import com.utility.ExcelBDDReader;
import com.utility.PropertyReader;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

import java.io.File;


public class Keyworddrivensteps {

ActionClass actions;
ExcelBDDReader excelBDDReader;
WebDriver driver;
GeneralInformation generalInformation;

public Keyworddrivensteps(DriverManager driverManager) {
try {
driver = driverManager.getDriver();
generalInformation = new GeneralInformation(driver);
excelBDDReader = new ExcelBDDReader();
actions = new ActionClass(driver);

} catch (Exception e) {
e.printStackTrace();
}
}

@Given("User executes test steps from {string} sheet in {string} and record the test steps to the feature file {string} and the stepdefination as {string}")
public void userExecutesTestStepsFromSheetInAndRecordTheTestStepsToTheFeatureFileAndTheStepdefinationAs(String arg0, String arg1, String arg2, String arg3) {

String Data = PropertyReader.getFieldValue(arg1);
File file = new File("./" + Data + "").getAbsoluteFile();
String filepath = "";
filepath += file;

String propstepdefnitionfilepath = PropertyReader.getFieldValue(arg3);
String propfeaturepath = PropertyReader.getFieldValue(arg2);

File stepdefnitionfile = new File("./" + propstepdefnitionfilepath + "").getAbsoluteFile();
String stepdefnitionfilefilepath = "";
stepdefnitionfilefilepath += stepdefnitionfile;

System.out.println("Executing test from Excel sheet: " + arg0 + " in file: " + Data);
//generalInformation.executeTest(Data, arg0);
excelBDDReader.mapToFeatureFile(filepath, arg0, propfeaturepath, stepdefnitionfilefilepath);
}


@Given("While filling the form navigate to Testzen Labs Form to proceed with registration")
public void whilefillingtheformnavigatetotestzenlabsformtoproceedwithregistration() {
}


@When("you correctly enter First Name before moving to the next field")
public void youcorrectlyenterfirstnamebeforemovingtothenextfield() {
}


@And("You should carefully enter Last Name so that it matches your official documents")
public void youshouldcarefullyenterlastnamesothatitmatchesyourofficialdocuments() {
}


@And("Before proceeding further make sure to enter Phone Number to receive OTP verification")
public void beforeproceedingfurthermakesuretoenterphonenumbertoreceiveotpverification() {
}


@Then("In the form select Country from the dropdown list to specify your nationality")
public void intheformselectcountryfromthedropdownlisttospecifyyournationality() {
}


@And("To complete your application kindly upload Resume in the specified format")
public void tocompleteyourapplicationkindlyuploadresumeinthespecifiedformat() {
}


@Then("check the Male option if applicable")
public void checkthemaleoptionifapplicable() {
}


@And("generate a random number for the pin code before submission")
public void generatearandomnumberforthepincodebeforesubmission() {
}




@Given("While 1 filling the form navigate to Testzen Labs Form to proceed with registration")
public void while_fillingtheformnavigatetotestzenlabsformtoproceedwithregistration() {
}



}
