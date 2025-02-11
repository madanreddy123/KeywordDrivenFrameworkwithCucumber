package com.base;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.List;

public class ActionClass {

    WebDriver driver;

    public ActionClass(WebDriver driver) {
        this.driver = driver;
    }


    public void waitForVisibilityOfElement(By locator, int timeinSec) {
        Wait wait = new FluentWait(driver).withTimeout(Duration.ofSeconds(timeinSec)).pollingEvery(Duration.ofSeconds(5L))
                .ignoring(NoSuchElementException.class);
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            throw e;
        }
    }

    public void waitforSeconds(long timeinSec) {
        try {
            Thread.sleep(1000*timeinSec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void click(By locator) {

        driver.findElement(locator).click();
    }

    public List<WebElement> findElemts(By locator) {

        return driver.findElements(locator);
    }

    public void clickUsingJS(By loc){
        WebElement element = driver.findElement(loc);
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("arguments[0].click();", element);
    }

    public void scrollByUsingJS(By loc){
        WebElement element = driver.findElement(loc);
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("arguments[0].scrollIntoView()", element);
    }


    public void sendKeys(By locator,String arg0) {
        driver.findElement(locator).sendKeys(arg0);
        String text = driver.findElement(locator).getAttribute("value");
        System.out.println(text);
    }

    public void clear(By locator) {
        driver.findElement(locator).clear();
    }

    public void fileUpload(String filepath) throws AWTException {
        StringSelection ss = new StringSelection(filepath);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }


}
