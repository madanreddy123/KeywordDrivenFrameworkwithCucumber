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
        String propstepdefnitionfolderfilepath = "";

        propstepdefnitionfolderfilepath+=  new File("src/main/java/com/stepdefination").getAbsoluteFile();

        String propfeaturepath = PropertyReader.getFieldValue(arg2);

        File stepdefnitionfile = new File("./" + propstepdefnitionfilepath + "").getAbsoluteFile();
        String stepdefnitionfilefilepath = "";
        stepdefnitionfilefilepath += stepdefnitionfile;

        System.out.println("Executing test from Excel sheet: " + arg0 + " in file: " + Data);
        generalInformation.executeTest(Data, arg0);
        excelBDDReader.mapToFeatureFile(filepath, arg0, propfeaturepath, propstepdefnitionfolderfilepath, stepdefnitionfilefilepath);
    }

}
