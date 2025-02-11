package com.pages;

import com.Enums.StepKeyword;
import com.base.ActionClass;
import com.utility.ExcelReader;
import com.utility.PropertyReader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GeneralInformation {
    WebDriver driver;
    ExcelReader excelReader;
    ActionClass actionClass;
    public GeneralInformation(WebDriver driver) {
        this.driver = driver;
        actionClass = new ActionClass(driver);
        excelReader = new ExcelReader();

    }

    public void executeTest(String excelFilePath, String sheetName) {
        try {
            List<Map<String, String>> testSteps = excelReader.getData(excelFilePath, sheetName);

            for (Map<String, String> step : testSteps) {
                String scenario = step.get("Scenario").trim();
                String stepNo = step.get("Step No").trim();
                String bddStep = step.get("BDD Steps").trim();
                String xpath = step.get("XPath").trim();
                String Additionalxpath = step.get("Additional XPath").trim();
                String inputData = step.get("Input Data").trim();

                System.out.println("Executing: " + bddStep);

                StepKeyword keyword = StepKeyword.fromBDDStep(bddStep);


                if (keyword == StepKeyword.SELECT_DROPDOWN && !inputData.isEmpty())
                {
                    keyword=StepKeyword.ENTER_TEXT;
                }


                switch (keyword) {

                    case NAVIGATE:
                        driver.get(inputData);
                        break;

                    case CLICK:
                        driver.findElement(By.xpath(xpath)).click();
                        break;

                    case ENTER_TEXT:
                        WebElement inputField = driver.findElement(By.xpath(xpath));
                        inputField.clear();
                        inputField.sendKeys(inputData);
                        Thread.sleep(2000);
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
                        break;

                    case SELECT_DROPDOWN:

                        driver.findElement(By.xpath(xpath)).click();
                        driver.findElement(By.xpath(Additionalxpath)).click();
                        break;

                    case UPLOAD_FILE:
                        WebElement uploadField = driver.findElement(By.xpath(xpath));
                        String getfile = String.valueOf(new File("./"+ PropertyReader.getFieldValue("sampledoc")+"").getAbsoluteFile());
                        uploadField.sendKeys(getfile);

                        break;

                    case CUSTOM_ACTION:
                        Random random = new Random();
                        int number = random.nextInt(100);
                        System.out.println(number);
                        actionClass.waitforSeconds(2);
                        actionClass.sendKeys(By.xpath(xpath), "12" +(number));
                        break;

                    default:
                        System.out.println("⚠️ Unrecognized step: " + bddStep);
                        break;
                }
            }
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
