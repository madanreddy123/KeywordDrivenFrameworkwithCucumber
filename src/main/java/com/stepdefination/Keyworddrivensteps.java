package com.stepdefination;

import com.base.ActionClass;
import com.base.DriverManager;
import com.pages.GeneralInformation;
import com.utility.PropertyReader;
import io.cucumber.java.en.Given;
import org.openqa.selenium.WebDriver;


public class Keyworddrivensteps {

    ActionClass actions;

    WebDriver driver;
    GeneralInformation generalInformation;
    public Keyworddrivensteps(DriverManager driverManager){
        try {
            driver = driverManager.getDriver();
            generalInformation = new GeneralInformation(driver);
            actions = new ActionClass(driver);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Given("User executes test steps from {string} sheet in {string}")
    public void userExecutesTestStepsFromSheetIn(String arg0, String arg1) {

        String Data = PropertyReader.getFieldValue(arg1);
        System.out.println("Executing test from Excel sheet: " + arg0 + " in file: " + Data);
        generalInformation.executeTest(Data, arg0);
    }
}
