package com.stepdefination;

import com.DynamicLocators;
import com.Enums.Tag;
import com.Tagspecifications.TagBasedSteps;
import com.base.ActionClass;
import com.base.DriverManager;
import com.pages.GeneralInformation;
import com.utility.ExcelBDDReader;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataTablesteps {

    ActionClass actions;
    ExcelBDDReader excelBDDReader;
    WebDriver driver;
    GeneralInformation generalInformation;
    DynamicLocators dynamicLocators;
    public DataTablesteps(DriverManager driverManager) {
        try {
            driver = driverManager.getDriver();
            generalInformation = new GeneralInformation(driver);
            dynamicLocators = new DynamicLocators(driver);
            actions = new ActionClass(driver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Given("I open the application {string}")
    public void iOpenTheApplication(String arg0) {

        driver.get(arg0);
    }

    @When("I enter the following details:")
    public void iEnterTheFollowingDetails(DataTable table) {


        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("Current Method: " + currentMethodName);

        String regex = "Enter";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(currentMethodName);
        System.out.println(matcher.pattern());
        String text = "";
        text+=matcher.pattern().toString().toLowerCase();
        System.out.println("🔍 BDD Step Detected: " + text);
        List<Tag> tags = TagBasedSteps.tags.getTagsBasedOnStep(text);
        List<Map<String, String>> data = table.asMaps(String.class, String.class);

        for (Map<String, String> row : data) {
            String field = row.get("Fields").trim();  // Extract "Field" name
            String value = row.get("Values").trim();  // Extract "Value" to input

            System.out.println("Entering: " + field + " = " + value);

            System.out.println("⌨ Entering text: " + value);

            String xpath = "";

            // If XPath is empty and tags are present, use dynamic XPath
            String inputxpath = xpath;
            if (xpath.isEmpty() && !tags.isEmpty()) {
                inputxpath = dynamicLocators.generateDynamicXPath(field, tags);
                System.out.println("Generated dynamic XPath for input: " + inputxpath);
            }
            System.out.println("xpath" + inputxpath);

            WebElement element1 = generalInformation.findElement(inputxpath, xpath);
            element1.clear();
            element1.sendKeys(value);
            if (element1.getAttribute("value").isEmpty())
            {
                element1.sendKeys(value);
            }


        }
    }



    @Then("click on the following:")
    public void clickOnTheFollowing(DataTable dataTable) {

        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("Current Method: " + currentMethodName);

        // Regex pattern to check if the string contains "Enter"
        String regex = "click";

        // Create the Pattern and Matcher
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(currentMethodName);
        System.out.println(matcher.pattern());
        String text = "";
        text += matcher.pattern().toString().toLowerCase();
        System.out.println("🔍 BDD Step Detected: " + text);
        List<Tag> tags = TagBasedSteps.tags.getTagsBasedOnStep(text);
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : data) {
            String field = row.get("Fields").trim();  // Extract "Field" name


             System.out.println("Clicking on: " + field);


            String xpath = "";

            // If XPath is empty and tags are present, use dynamic XPath
            String inputxpath = xpath;
            if (xpath.isEmpty() && !tags.isEmpty()) {
                inputxpath = dynamicLocators.generateDynamicXPath(field, tags);
                System.out.println("Generated dynamic XPath for input: " + inputxpath);
            }
            System.out.println("xpath" + inputxpath);

            WebElement element = generalInformation.findElement(inputxpath, xpath);
            if (element != null) {
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("arguments[0].click();", element);
                actions.waitforSeconds(1);
                if (!element.isSelected()) {
                    element.click();
                }
            } else {
                System.err.println("🚨 Could not find element for label: " + inputxpath);
            }


        }

    }

    @And("select the check box as following:")
    public void selectTheCheckBoxAsFollowing(DataTable dataTable) {


        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("Current Method: " + currentMethodName);

        // Regex pattern to check if the string contains "Enter"
        String regex = "checkbox";

        // Create the Pattern and Matcher
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(currentMethodName);
        System.out.println(matcher.pattern());
        String text = "";
        text += matcher.pattern().toString().toLowerCase();
        System.out.println("🔍 BDD Step Detected: " + text);
        List<Tag> tags = TagBasedSteps.tags.getTagsBasedOnStep(text);
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : data) {
            String field = row.get("Fields").trim();  // Extract "Field" name


            System.out.println("Clicking on: " + field);


            String xpath = "";

            // If XPath is empty and tags are present, use dynamic XPath
            String inputxpath = xpath;
            if (xpath.isEmpty() && !tags.isEmpty()) {
                inputxpath = dynamicLocators.generateDynamiccheckXPath(field, tags);
                System.out.println("Generated dynamic XPath for input: " + inputxpath);
            }
            System.out.println("xpath" + inputxpath);

            WebElement element = generalInformation.findElement(inputxpath, xpath);
            if (element != null) {
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("arguments[0].click();", element);
                if (!element.isSelected()) {
                    element.click();
                }
            } else {
                System.err.println("🚨 Could not find element for label: " + inputxpath);
            }


        }
    }

    @And("select the radio button as following:")
    public void selectTheRadioButtonAsFollowing(DataTable dataTable) {

        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("Current Method: " + currentMethodName);

        // Regex pattern to check if the string contains "Enter"
        String regex = "radiobutton";

        // Create the Pattern and Matcher
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(currentMethodName);
        System.out.println(matcher.pattern());
        String text = "";
        text += matcher.pattern().toString().toLowerCase();
        System.out.println("🔍 BDD Step Detected: " + text);
        List<Tag> tags = TagBasedSteps.tags.getTagsBasedOnStep(text);
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : data) {
            String field = row.get("Fields").trim();  // Extract "Field" name


            System.out.println("Clicking on: " + field);


            String xpath = "";

            // If XPath is empty and tags are present, use dynamic XPath
            String inputxpath = xpath;
            if (xpath.isEmpty() && !tags.isEmpty()) {
                inputxpath = dynamicLocators.generateDynamicradiobuttonXPath(field, tags);
                System.out.println("Generated dynamic XPath for input: " + inputxpath);
            }
            System.out.println("xpath" + inputxpath);

            WebElement element = generalInformation.findElement(inputxpath, xpath);
            if (element != null) {
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("arguments[0].click();", element);
                if (!element.isSelected()) {
                    element.click();
                }
            } else {
                System.err.println("🚨 Could not find element for label: " + inputxpath);
            }


        }

    }

    @Then("choose the option {string} from the dropdown {string}")
    public void chooseTheOptionFromTheDropdown(String arg0, String arg1) {


        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("Current Method: " + currentMethodName);

        // Regex pattern to check if the string contains "Enter"
        String regex = "choose";

        // Create the Pattern and Matcher
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(currentMethodName);
        System.out.println(matcher.pattern());
        String text = "";
        text += matcher.pattern().toString().toLowerCase();
        System.out.println("🔍 BDD Step Detected: " + text);
        List<Tag> tags = TagBasedSteps.tags.getTagsBasedOnStep(text);

            System.out.println("Clicking on: " + arg1);


            String xpath = "";

            // If XPath is empty and tags are present, use dynamic XPath
            String inputxpath = xpath;
            if (xpath.isEmpty() && !tags.isEmpty()) {
                inputxpath = dynamicLocators.generateDynamicXPath(arg1, tags);
                System.out.println("Generated dynamic XPath for input: " + inputxpath);
            }
            System.out.println("xpath" + inputxpath);

            WebElement element = generalInformation.findElement(inputxpath, xpath);
            if (element != null) {
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("arguments[0].click();", element);
                actions.waitforSeconds(1);

            } else {
                System.err.println("🚨 Could not find element for label: " + inputxpath);
            }

        if (xpath.isEmpty() && !tags.isEmpty()) {
            inputxpath = dynamicLocators.generateDynamicXPath(arg0, tags);
            System.out.println("Generated dynamic XPath for input: " + inputxpath);
        }
        System.out.println("xpath" + inputxpath);

        element = generalInformation.findElement(inputxpath, xpath);
        if (element != null) {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", element);
            actions.waitforSeconds(1);
        } else {
            System.err.println("🚨 Could not find element for label: " + inputxpath);
        }

    }
}
