package com.pages;

import com.Enums.StepKeyword;
import com.base.ActionClass;
import com.utility.ExcelReader;
import com.utility.PropertyReader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openqa.selenium.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

    public class Tagstakingfromtheexcelfilegeneralinformation {
        WebDriver driver;
        ExcelReader excelReader;
        ActionClass actionClass;

        public Tagstakingfromtheexcelfilegeneralinformation(WebDriver driver) {
            this.driver = driver;
            actionClass = new ActionClass(driver);
            excelReader = new ExcelReader();
        }

        /**
         * Generate a dynamic XPath based on BDD step text.
         * This XPath will be case-insensitive to handle lowercase or uppercase in BDD steps.
         */
        public String generateDynamicClickXPath(String fieldText, String tag) {

            String searchText = fieldText.substring(fieldText.indexOf("\"") + 1, fieldText.lastIndexOf("\"")).trim();

            // Return the XPath with the corrected structure for a dynamic click
            String xpath = "//" + tag + "[" +
                    "contains(translate(@placeholder, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "')]";

            // Fallback if none of the attributes are found
            String fallbackXPath = "//" + tag + "[contains(@class, '" + searchText + "') or contains(@id, '" + searchText + "')]";

            // Return the main XPath with the fallback if no attributes match
            return "(" + xpath + ") | " + fallbackXPath;
        }






        /**
         * Find an element by dynamic XPath, fallback to Excel XPath if not found.
         */
        public WebElement findElement(String dynamicXPath, String excelXPath) {
            try {
                // Try to find the element using the dynamic XPath
                return driver.findElement(By.xpath(dynamicXPath));
            } catch (NoSuchElementException e) {
                // If not found, fall back to the Excel XPath
                return driver.findElement(By.xpath(excelXPath));
            }
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
                    String tags = String.valueOf(step.get("Tags")).trim();
                    String inputData = String.valueOf(step.get("Input Data")).trim();

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

                                WebElement element = findElement(dynamicXPath, xpath);
                                element.click();
                                break;

                            case ENTER_TEXT:
                                System.out.println("⌨ Entering text: " + inputData);

                                // If XPath is empty and tags are present, use dynamic XPath
                                String inputxpath = xpath;
                                if (xpath.isEmpty() && !tags.isEmpty()) {
                                    inputxpath = generateDynamicClickXPath(bddStep, tags);
                                    System.out.println("Generated dynamic XPath for input: " + inputxpath);
                                }

                                WebElement element1 = findElement(inputxpath, xpath);
                                element1.clear();
                                element1.sendKeys(inputData);
                                break;

                            case VERIFY_TEXT:
                                String actualText = driver.findElement(By.xpath(xpath)).getText();
                                if (!actualText.equals(inputData)) {
                                    System.out.println("❌ Verification Failed: Expected - " + inputData + ", Found - " + actualText);
                                } else {
                                    System.out.println("✅ Verification Passed");
                                }
                                break;

                            case CHECKBOX:
                                WebElement checkbox = driver.findElement(By.xpath(xpath));
                                if (!checkbox.isSelected()) {
                                    checkbox.click();
                                }
                                System.out.println("☑ Checked: " + xpath);
                                break;

                            case SELECT_DROPDOWN:
                                System.out.println("📌 Selecting from dropdown: " + inputData);
                                driver.findElement(By.xpath(xpath)).click();
                                driver.findElement(By.xpath(additionalXpath)).click();
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

    }

