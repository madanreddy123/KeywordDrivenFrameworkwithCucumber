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

    /**
     * Find an element by dynamic XPath, fallback to Excel XPath if not found.
     */
    public WebElement findElement(By dynamicXPath) {

            // Try to find the element using the dynamic XPath
            return driver.findElement(dynamicXPath);

    }

    /**
     * Executes test steps from an Excel sheet.
     * It navigates to the URL first, then performs actions like click, enter text, verify text, etc.
     */
    public void executeTest(String excelFilePath, String sheetName) {
        try {
            List<Map<String, String>> testSteps = excelReader.getData(excelFilePath, sheetName);

            for (Map<String, String> step : testSteps) {
                String scenario = String.valueOf(step.get("Scenario")).trim();
                String stepNo = String.valueOf(step.get("Step No")).trim();
                String bddStep = String.valueOf(step.get("BDD Steps")).trim();
                String xpath = String.valueOf(step.get("XPath")).trim();
                String additionalXpath = String.valueOf(step.get("Additional XPath")).trim();
                String inputData = String.valueOf(step.get("Input Data")).trim();

                // Instead of extracting tags from the Excel file,
                // use predefined tags based on the step keyword
                List<Tag> tags = getTagsBasedOnStep(bddStep);

                System.out.println("🔹 Executing Step: " + bddStep);

                StepKeyword keyword = StepKeyword.fromBDDStep(bddStep);
                try {
                    switch (keyword) {
                        case NAVIGATE:
                            System.out.println("🌐 Navigating to: " + inputData);
                            driver.get(inputData);
                            break;

                        case CLICK:
                            System.out.println("🖱 Clicking element: " + xpath);

                            // If XPath is empty and tags are present, use dynamic XPath
                            String dynamicXPath = xpath;
                            if (xpath.isEmpty() && !tags.isEmpty()) {
                                dynamicXPath = generateDynamicClickXPath(bddStep, tags);
                                System.out.println("Generated dynamic XPath: " + dynamicXPath);
                            }

                            WebElement element = findElement(By.xpath(xpath));
                            JavascriptExecutor executor = (JavascriptExecutor)driver;
                            executor.executeScript("arguments[0].click();", element);
                            if (!element.isSelected())
                            {
                                element.click();
                            }
                            System.out.println("🖱 Clicked element: " + xpath);
                        break;

                        case ENTER_TEXT:
                            System.out.println("⌨ Entering text: " + inputData);

                            // If XPath is empty and tags are present, use dynamic XPath
                            String inputxpath = xpath;
                            if (xpath.isEmpty() && !tags.isEmpty()) {
                                inputxpath = generateDynamicClickXPath(bddStep, tags);
                                System.out.println("Generated dynamic XPath for input: " + inputxpath);
                            }

                            WebElement element1 = findElement(By.xpath(xpath));
                            element1.clear();
                            element1.sendKeys(inputData);
                            if (element1.getAttribute("value").isEmpty())
                            {
                                element1.sendKeys(inputData);
                            }
                            break;

                        case VERIFY_TEXT:
                            // If XPath is empty and tags are present, use dynamic XPath
                           inputxpath = xpath;
                            if (xpath.isEmpty() && !tags.isEmpty()) {
                                inputxpath = generateDynamicClickXPath(bddStep, tags);
                                System.out.println("Generated dynamic XPath for input: " + inputxpath);
                            }

                            actionClass.waitForVisibilityOfElement(By.xpath(inputxpath), 3);
                            String actualText = driver.findElement(By.xpath(inputxpath)).getText();
                            if (!actualText.equals(inputData)) {
                                System.out.println("❌ Verification Failed: Expected - " + inputData + ", Found - " + actualText);
                            } else {
                                System.out.println("✅ Verification Passed");
                            }
                            break;

                        case CHECKBOX:
                            // If XPath is empty and tags are present, use dynamic XPath
                           inputxpath = xpath;
                            if (xpath.isEmpty() && !tags.isEmpty()) {
                                inputxpath = generateDynamicClickXPath(bddStep, tags);
                                System.out.println("Generated dynamic XPath for input: " + inputxpath);
                            }

                            WebElement checkbox = findElement(By.xpath(xpath));
                            if (!checkbox.isSelected()) {
                                checkbox.click();
                            }
                            System.out.println("☑ Checked: " + xpath);
                            break;

                        case SELECT_DROPDOWN:
                            System.out.println("📌 Selecting from dropdown: " + inputData);
                            // If XPath is empty and tags are present, use dynamic XPath
                            inputxpath = xpath;
                            if (xpath.isEmpty() && !tags.isEmpty()) {
                                inputxpath = generateDynamicClickXPath(bddStep, tags);
                                System.out.println("Generated dynamic XPath for input: " + inputxpath);
                            }

                            if (inputxpath.contains("//select")) {
                                actionClass.waitForVisibilityOfElement(By.xpath(inputxpath), 13);
                                Select select = new Select(driver.findElement(By.xpath(inputxpath)));
                                    String result = "";
                                       result+=extractOption(bddStep);
                                    System.out.println(result);
                                    select.selectByVisibleText(result);
                                System.out.println("Selected option : " + result);

                            }

                            break;

                        case CHOOSE_DROPDOWN:
                            System.out.println("📌 Selecting from dropdown: " + inputData);
                            // If XPath is empty and tags are present, use dynamic XPath
                            inputxpath = xpath;
                            if (xpath.isEmpty() && !tags.isEmpty()) {
                                inputxpath = generateDynamicClickXPath(bddStep, tags);
                                System.out.println("Generated dynamic XPath for input: " + inputxpath);
                            }

                            System.out.println(inputxpath);

                                actionClass.waitForVisibilityOfElement(By.xpath(inputxpath), 3);
                               actionClass.clickUsingJS(By.xpath(inputxpath));

                               actionClass.waitforSeconds(1);
                                String result = "";
                                result+=extractOption(bddStep);
                                String resultloc = result.trim();

                                String additionalloc = additionalXpath;
                                if (additionalloc.isEmpty())
                                {
                                    additionalloc  = "//*[contains(text(), '"+resultloc+"')]";

                                }
                                System.out.println(additionalloc);
                                actionClass.waitForVisibilityOfElement(By.xpath(additionalloc), 3);
                                actionClass.clickUsingJS(By.xpath(additionalloc));
                                actionClass.waitforSeconds(1);


                            break;

                        case UPLOAD_FILE:
                            WebElement uploadField = driver.findElement(By.xpath(xpath));
                            String filePath = new File("./" + PropertyReader.getFieldValue("sampledoc")).getAbsolutePath();
                            uploadField.sendKeys(filePath);
                            System.out.println("📂 Uploaded file: " + filePath);
                            break;

                        case CUSTOM_ACTION:
                            Random random = new Random();
                            int number = random.nextInt(10000); // Generates 4-digit number
                            System.out.println("🔢 Generated Random Number: " + number);
                            actionClass.sendKeys(By.xpath(xpath), "12" + number);
                            break;

                        // Handle actions for tags dynamically (Example: Input, A, etc.)
                        default:
                            if (bddStep.isEmpty()) {
                                break;
                            } else {
                                System.out.println("⚠️ Unrecognized step: " + bddStep);
                            }
                            break;
                    }
                } catch (NoSuchElementException e) {
                    System.err.println("🚨 Element Not Found: " + xpath);
                } catch (TimeoutException e) {
                    System.err.println("⏳ Timeout on Step: " + bddStep);
                }
            }
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
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
