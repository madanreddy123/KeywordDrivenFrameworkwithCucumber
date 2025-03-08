package com.pages;

import com.Enums.StepKeyword;
import com.Enums.Tag;
import com.base.ActionClass;
import com.utility.ExcelReader;
import com.utility.PropertyReader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GeneralInformation {
    WebDriver driver;
    ExcelReader excelReader;
    ActionClass actionClass;

    public GeneralInformation(WebDriver driver) {
        this.driver = driver;
        actionClass = new ActionClass(driver);
        excelReader = new ExcelReader();
    }

   static String scenario;
   static String stepNo;
    static String bddStep;
    static String xpath;
   static String additionalXpath;
  static   String inputData;

    /**
     * Generate a dynamic XPath based on BDD step text.
     * This XPath will be case-insensitive to handle lowercase or uppercase in BDD steps.
     */
    public String generateDynamicClickXPath(String fieldText, List<Tag> tags) {
        String searchText = fieldText.substring(fieldText.indexOf("\"") + 1, fieldText.lastIndexOf("\"")).trim();
        if (searchText.isEmpty()) {
            System.err.println("🚨 The search text from the BDD step is empty: " + fieldText);
            return ""; // Return empty to handle this case
        }

        StringBuilder xpathBuilder = new StringBuilder();

        // Iterate over tags and build XPath
        for (Tag tag : tags) {
            xpathBuilder.append("//").append(tag.getTagValue()).append("[" +
                    "contains(translate(@placeholder, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "')]").append(" | ");
        }

        // Fallback XPath
        xpathBuilder.append("//").append(tags.get(0).getTagValue()).append("[contains(@class, '" + searchText + "') or contains(@id, '" + searchText + "')]");

        String finalXPath = xpathBuilder.toString().trim();
        if (finalXPath.isEmpty()) {
            System.err.println("🚨 Generated XPath is empty.");
        }

        // Debugging line to see the generated XPath
        System.out.println("Generated XPath: " + finalXPath);

        return finalXPath;
    }

    public WebElement findElement(By... dynamicXpaths) {
        for (By xpath : dynamicXpaths) {
            try {
                return driver.findElement(xpath);
            } catch (NoSuchElementException e) {
                System.err.println("🚨 Element Not Found for XPath: " + xpath);
            }
        }
        throw new NoSuchElementException("None of the provided XPaths found an element.");
    }

    public void printWebElements(WebElement... elements) {
        for (WebElement element : elements) {
            System.out.println(element.getText());
        }
    }

    public void executeTest(String excelFilePath, String sheetName) {
        try {
            List<Map<String, String>> testSteps = excelReader.getData(excelFilePath, sheetName);

            for (Map<String, String> step : testSteps) {
                scenario = step.get("Scenario").trim();
                stepNo = step.get("Step No").trim();
                bddStep = step.get("BDD Steps").trim();
                xpath = step.get("XPath").trim();
                additionalXpath = step.get("Additional XPath").trim();
                inputData = step.get("Input Data").trim();

                List<Tag> tags = getTagsBasedOnStep(bddStep);
                System.out.println("🔹 Executing Step: " + bddStep);
                StepKeyword keyword = StepKeyword.fromBDDStep(bddStep);

                try {
                    switch (keyword) {
                        case NAVIGATE:
                            System.out.println("🌍 Navigating to: " + inputData);
                            driver.get(inputData);
                            break;

                        case CLICK:
                            System.out.println("🖱️ Clicking element: " + xpath);
                            String dynamicXPath = xpath.isEmpty() && !tags.isEmpty() ? generateDynamicClickXPath(bddStep, tags) : xpath;
                            WebElement element = findElement(By.xpath(dynamicXPath));
                            clickElement(element);
                            break;

                        case ENTER_TEXT:
                            System.out.println("⌨️ Entering text: " + inputData);
                            String inputXpath = xpath.isEmpty() && !tags.isEmpty() ? generateDynamicClickXPath(bddStep, tags) : xpath;
                            WebElement inputElement = findElement(By.xpath(inputXpath));
                            enterText(inputElement, inputData);
                            break;

                        case VERIFY_TEXT:
                            inputXpath = xpath.isEmpty() && !tags.isEmpty() ? generateDynamicClickXPath(bddStep, tags) : xpath;
                            verifyText(inputXpath, inputData);
                            break;

                        case CHECKBOX:
                            System.out.println("☑️ Checking checkbox: " + xpath);
                            WebElement checkbox = findElement(By.xpath(xpath.isEmpty() ? generateDynamicClickXPath(bddStep, tags) : xpath));
                            checkCheckbox(checkbox);
                            break;

                        case SELECT_DROPDOWN:
                            System.out.println("🔽 Selecting dropdown: " + inputData);
                            String selectXpath = xpath.isEmpty() && !tags.isEmpty() ? generateDynamicClickXPath(bddStep, tags) : xpath;
                            selectDropdown(selectXpath, additionalXpath, bddStep);
                            break;

                        case UPLOAD_FILE:
                            System.out.println("📂 Uploading file: " + xpath);
                            uploadFile(xpath);
                            break;

                        case CUSTOM_ACTION:
                            System.out.println("🔢 Performing custom action with random number.");
                            performCustomAction(xpath);
                            break;

                        default:
                            if (bddStep.isEmpty()) {
                                break;
                            } else {
                                System.out.println("⚠️ Unrecognized step: " + bddStep);
                            }
                            break;
                    }
                } catch (NoSuchElementException | TimeoutException e) {
                    System.err.println("⏳ Error in Step: " + bddStep + " - " + e.getMessage());
                }
            }
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    // Action Methods with Symbols
    public void clickElement(WebElement... elements) {
        for (WebElement element : elements) {
            try {
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("arguments[0].click();", element);
                if (!element.isSelected()) {
                    element.click();
                }
                System.out.println("🖱️ Clicked element: " + element.getText());
            } catch (Exception e) {
                System.err.println("❌ Click action failed: " + e.getMessage());
            }
        }
    }

    public void enterText(WebElement element, String... textInputs) {
        for (String text : textInputs) {
            element.clear();
            element.sendKeys(text);
            if (element.getAttribute("value").isEmpty()) {
                element.sendKeys(text);
            }
            System.out.println("⌨️ Entered text: " + text);
        }
    }

    public void verifyText(String xpath, String... expectedTexts) {
        actionClass.waitForVisibilityOfElement(By.xpath(xpath), 3);
        String actualText = driver.findElement(By.xpath(xpath)).getText();
        for (String expectedText : expectedTexts) {
            if (!actualText.equals(expectedText)) {
                System.out.println("❌ Verification Failed: Expected - " + expectedText + ", Found - " + actualText);
            } else {
                System.out.println("✅ Verification Passed");
            }
        }
    }

    public void checkCheckbox(WebElement... checkboxes) {
        for (WebElement checkbox : checkboxes) {
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
            System.out.println("☑️ Checked checkbox: " + checkbox.getText());
        }
    }

    public void selectDropdown(String selectXpath, String additionalXpath, String bddStep) {
        if (selectXpath.contains("//select")) {
            actionClass.waitForVisibilityOfElement(By.xpath(selectXpath), 3);
            Select select = new Select(driver.findElement(By.xpath(selectXpath)));
            select.selectByVisibleText(inputData);
            System.out.println("🔽 Selected dropdown option: " + inputData);
        } else {
            actionClass.waitForVisibilityOfElement(By.xpath(selectXpath), 3);
            actionClass.clickUsingJS(By.xpath(selectXpath));
            actionClass.waitforSeconds(1);
            String option = extractOption(bddStep);
            String optionXpath = additionalXpath.isEmpty() ? "//*[contains(text(), '" + option.trim() + "')]" : additionalXpath;
            actionClass.waitForVisibilityOfElement(By.xpath(optionXpath), 3);
            actionClass.clickUsingJS(By.xpath(optionXpath));
            actionClass.waitforSeconds(1);
            System.out.println("🔽 Selected option from dropdown: " + option);
        }
    }

    public void uploadFile(String xpath) {
        WebElement uploadField = driver.findElement(By.xpath(xpath));
        String filePath = new File("./" + PropertyReader.getFieldValue("sampledoc")).getAbsolutePath();
        uploadField.sendKeys(filePath);
        System.out.println("📂 Uploaded file: " + filePath);
    }

    public void performCustomAction(String xpath) {
        int number = new Random().nextInt(10000);
        System.out.println("🔢 Generated Random Number: " + number);
        actionClass.sendKeys(By.xpath(xpath), "12" + number);
    }


    /**
     * Helper method to get tags based on the BDD Step.
     */
    private List<Tag> getTagsBasedOnStep(String bddStep) {

        List<Tag> tags = new ArrayList<>();
        String bddstep = bddStep.toLowerCase().trim();


        if (bddstep.contains("click")) {
            tags.add(Tag.INPUT);
            tags.add(Tag.A);
            tags.add(Tag.DIV);
            tags.add(Tag.BUTTON);

        }  else if(bddstep.contains("enter")){

            tags.add(Tag.INPUT);
            tags.add(Tag.TEXTAREA);
        }

        else if(bddstep.contains("validate")){

            tags.add(Tag.DIV);
            tags.add(Tag.SPAN);
            tags.add(Tag.A);
        }

        else if(bddstep.contains("select")){

            tags.add(Tag.SELECT);
        }

        else if(bddstep.contains("pick")){

            tags.add(Tag.A);
        }

        return tags;
    }

        public static String extractOption(String input) {
            // Regular expression to capture the string between 'option' and 'from'
            String regex = "(?<=option\\s)(.*?)(?=\\sfrom)";

            // Matching the pattern
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
            java.util.regex.Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                return matcher.group(1);  // Return the matched string
            }
            return null;  // Return null if no match found
        }
}
