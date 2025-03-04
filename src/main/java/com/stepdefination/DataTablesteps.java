package com.stepdefination;

import com.Tagspecifications.DynamicLocators;
import com.Enums.Tag;
import com.Tagspecifications.TagBasedSteps;
import com.base.ActionClass;
import com.base.DriverManager;
import com.pages.GeneralInformation;
import com.utility.ExcelBDDReader;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.openqa.selenium.By;
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
        text += matcher.pattern().toString().toLowerCase();
        System.out.println("🔍 BDD Step Detected: " + text);

        List<Tag> tags = TagBasedSteps.tags.getTagsBasedOnStep(text);
        List<Map<String, String>> data = table.asMaps(String.class, String.class);

        for (Map<String, String> row : data) {
            String field = row.get("Fields").trim();  // Extract "Field" name
            String value = row.get("Values").trim();  // Extract "Value" to input
            String xpath = row.get("Xpath");  // Extract XPath value from table

            String xpathloc = "";
            try {
                if (!xpath.isEmpty())
                {
                    xpathloc+= xpath.trim();
                }
            }
           catch (NullPointerException e)
           {
               System.err.println(e.getMessage());
           }

            System.out.println("Entering: " + field + " = " + value);

            System.out.println("⌨ Entering text: " + value);

            WebElement element1 = null;

            // First, check if the provided XPath is valid
            if (!xpathloc.isEmpty()) {
                try {
                    element1 = generalInformation.findElement(By.xpath(xpath));
                    if (element1 != null) {
                        System.out.println("Found element using provided XPath: " + xpath);
                    }
                } catch (Exception e) {
                    System.out.println("Invalid XPath: " + xpath + ", falling back to dynamic XPath generation.");
                }
            }

            // If the XPath is invalid or element not found, generate a dynamic XPath
            if (element1 == null && !tags.isEmpty()) {
                String dynamicXpath = dynamicLocators.generateDynamicXPathforinput(field, tags);
                try {
                    element1 = generalInformation.findElement(By.xpath(dynamicXpath));
                    if (element1 != null) {
                        System.out.println("Found element using dynamic XPath: " + dynamicXpath);
                    }
                } catch (Exception e) {
                    System.out.println("Error finding element using dynamic XPath: " + dynamicXpath);
                }
            }

            // If the element is found, interact with it
            if (element1 != null) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].style.border='3px solid red';", element1); // Optional to highlight the element
                element1.clear();
                element1.sendKeys(value);
                if (element1.getAttribute("value").isEmpty()) {
                    element1.sendKeys(value);  // In case the input didn't register
                }
            } else {
                System.out.println("Could not find element for field: " + field);
            }
        }
    }




    @Then("click on the following:")
    public void clickOnTheFollowing(DataTable dataTable) {
        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("Current Method: " + currentMethodName);

        String regex = "click";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(currentMethodName);
        String text = matcher.pattern().toString().toLowerCase();
        System.out.println("🔍 BDD Step Detected: " + text);

        List<Tag> tags = TagBasedSteps.tags.getTagsBasedOnStep(text);
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : data) {
            String field = row.get("Fields").trim();
            System.out.println("Clicking on: " + field);

            String xpath = row.get("Xpath");

          String  xpathlo = "";
          try {
              xpathlo += xpath.trim();
          }
          catch (NullPointerException e){
              System.out.println(e.getMessage());
          }


            WebElement element = null;

            // First, try to use the provided XPath
            if (!xpathlo.isEmpty()) {
                try {
                    element = generalInformation.findElement(By.xpath(xpath));
                    if (element != null) {
                        System.out.println("Found element using provided XPath: " + xpath);
                    }
                } catch (Exception e) {
                    System.out.println("Invalid XPath: " + xpath + ", falling back to dynamic XPath.");
                }
            }

            // If provided XPath is invalid or empty, fallback to dynamic XPath
            if (element == null && !tags.isEmpty()) {
                String dynamicXpath = dynamicLocators.generateDynamicXPathforclick(field, tags);
                try {
                    element = generalInformation.findElement(By.xpath(dynamicXpath));
                    if (element != null) {
                        System.out.println("Found element using dynamic XPath: " + dynamicXpath);
                    }
                } catch (Exception e) {
                    System.err.println("Could not find element using dynamic XPath: " + dynamicXpath);
                }
            }

            // Perform click if element found
            if (element != null) {
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("arguments[0].style.border='3px solid red';", element);
                executor.executeScript("arguments[0].click();", element);
                actions.waitforSeconds(1);
            } else {
                System.err.println("🚨 Could not find element for label: " + field);
            }
        }
    }

    @And("select the check box as following:")
    public void selectTheCheckBoxAsFollowing(DataTable dataTable) {
        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("Current Method: " + currentMethodName);

        String regex = "checkbox";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(currentMethodName);
        String text = matcher.pattern().toString().toLowerCase();
        System.out.println("🔍 BDD Step Detected: " + text);

        List<Tag> tags = TagBasedSteps.tags.getTagsBasedOnStep(text);
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : data) {
            String field = row.get("Fields").trim();
            System.out.println("Selecting checkbox for: " + field);

            String xpath = row.get("Xpath").trim();  // Extract XPath

            WebElement element = null;

            // First, try to use the provided XPath
            if (!xpath.isEmpty()) {
                try {
                    element = generalInformation.findElement(By.xpath(xpath));
                    if (element != null) {
                        System.out.println("Found element using provided XPath: " + xpath);
                    }
                } catch (Exception e) {
                    System.out.println("Invalid XPath: " + xpath + ", falling back to dynamic XPath.");
                }
            }

            // If provided XPath is invalid or empty, fallback to dynamic XPath
            if (element == null && !tags.isEmpty()) {
                String dynamicXpath = dynamicLocators.generateDynamiccheckXPath(field, tags);
                try {
                    element = generalInformation.findElement(By.xpath(dynamicXpath));
                    if (element != null) {
                        System.out.println("Found element using dynamic XPath: " + dynamicXpath);
                    }
                } catch (Exception e) {
                    System.err.println("Could not find element using dynamic XPath: " + dynamicXpath);
                }
            }

            // Perform click if element found
            if (element != null) {
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("arguments[0].style.border='3px solid red';", element);
                executor.executeScript("arguments[0].click();", element);
                if (!element.isSelected()) {
                    element.click();
                }
            } else {
                System.err.println("🚨 Could not find element for label: " + field);
            }
        }
    }

    @And("select the radio button as following:")
    public void selectTheRadioButtonAsFollowing(DataTable dataTable) {
        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("Current Method: " + currentMethodName);

        String regex = "radiobutton";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(currentMethodName);
        String text = matcher.pattern().toString().toLowerCase();
        System.out.println("🔍 BDD Step Detected: " + text);

        List<Tag> tags = TagBasedSteps.tags.getTagsBasedOnStep(text);
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : data) {
            String field = row.get("Fields").trim();
            System.out.println("Selecting radio button for: " + field);

            String xpath = row.get("Xpath").trim();  // Extract XPath

            WebElement element = null;

            // First, try to use the provided XPath
            if (!xpath.isEmpty()) {
                try {
                    element = generalInformation.findElement(By.xpath(xpath));
                    if (element != null) {
                        System.out.println("Found element using provided XPath: " + xpath);
                    }
                } catch (Exception e) {
                    System.out.println("Invalid XPath: " + xpath + ", falling back to dynamic XPath.");
                }
            }

            // If provided XPath is invalid or empty, fallback to dynamic XPath
            if (element == null && !tags.isEmpty()) {
                String dynamicXpath = dynamicLocators.generateDynamicradiobuttonXPath(field, tags);
                try {
                    element = generalInformation.findElement(By.xpath(dynamicXpath));
                    if (element != null) {
                        System.out.println("Found element using dynamic XPath: " + dynamicXpath);
                    }
                } catch (Exception e) {
                    System.err.println("Could not find element using dynamic XPath: " + dynamicXpath);
                }
            }

            // Perform click if element found
            if (element != null) {
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("arguments[0].style.border='3px solid red';", element);
                executor.executeScript("arguments[0].click();", element);
                if (!element.isSelected()) {
                    element.click();
                }
            } else {
                System.err.println("🚨 Could not find element for label: " + field);
            }
        }
    }

    @Then("choose the option {string} from the dropdown {string}")
    public void chooseTheOptionFromTheDropdown(String arg0, String arg1) {
        String currentMethodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        System.out.println("Current Method: " + currentMethodName);

        String regex = "choose";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(currentMethodName);
        String text = matcher.pattern().toString().toLowerCase();
        System.out.println("🔍 BDD Step Detected: " + text);

        List<Tag> tags = TagBasedSteps.tags.getTagsBasedOnStep(text);
        String field = arg1;

        System.out.println("Choosing option for dropdown: " + field);

        String xpath = "";

        WebElement element = null;

        // First, try to use the provided XPath
        if (!xpath.isEmpty()) {
            try {
                element = generalInformation.findElement(By.xpath(xpath));
                if (element != null) {
                    System.out.println("Found element using provided XPath: " + xpath);
                }
            } catch (Exception e) {
                System.out.println("Invalid XPath: " + xpath + ", falling back to dynamic XPath.");
            }
        }

        // If provided XPath is invalid or empty, fallback to dynamic XPath
        if (element == null && !tags.isEmpty()) {
            String dynamicXpath = dynamicLocators.generateDynamicXPathforclick(field, tags);
            try {
                element = generalInformation.findElement(By.xpath(dynamicXpath));
                if (element != null) {
                    System.out.println("Found element using dynamic XPath: " + dynamicXpath);
                }
            } catch (Exception e) {
                System.err.println("Could not find element using dynamic XPath: " + dynamicXpath);
            }
        }

        // Perform click if element found
        if (element != null) {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].style.border='3px solid red';", element);
            executor.executeScript("arguments[0].click();", element);
            actions.waitforSeconds(1);
        } else {
            System.err.println("🚨 Could not find element for label: " + field);
        }

        // Now select the option based on argument
        if (!tags.isEmpty()) {
            String dynamicXpath = dynamicLocators.generateDynamicXPathforclick(arg0, tags);
            try {
                element = generalInformation.findElement(By.xpath(dynamicXpath));
                if (element != null) {
                    System.out.println("Found element using dynamic XPath for option: " + dynamicXpath);
                    element.click();
                }
            } catch (Exception e) {
                System.err.println("Error selecting option from dropdown: " + dynamicXpath);
            }
        }
    }


    @Then("wait for the element")
    public void waitForTheElement() {

        actions.waitforSeconds(1);
    }
}
