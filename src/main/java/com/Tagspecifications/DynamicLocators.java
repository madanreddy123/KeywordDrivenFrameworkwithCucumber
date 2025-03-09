package com.Tagspecifications;

import com.Enums.Tag;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DynamicLocators {
    WebDriver driver;

    public DynamicLocators(WebDriver driver) {
        this.driver = driver;
    }
    public  String generateXPath(WebDriver driver, String fieldName) {
        List<WebElement> elements = driver.findElements(By.xpath(
                "//*[contains(text(), '" + fieldName + "') " +
                        "or contains(@id, '" + fieldName + "') " +
                        "or contains(@name, '" + fieldName + "') " +
                        "or contains(@class, '" + fieldName + "') " +
                        "or contains(@placeholder, '" + fieldName + "') " +
                        "or contains(@aria-label, '" + fieldName + "')]"
        ));

        if (elements.isEmpty()) {
            return "Field not found";
        }

        for (WebElement element : elements) {
            String tag = element.getTagName();
            String id = element.getAttribute("id");
            String name = element.getAttribute("name");
            String placeholder = element.getAttribute("placeholder");
            String ariaLabel = element.getAttribute("aria-label");
            String text = element.getText();

            // Unique Attribute-Based XPath
            if (id != null && !id.isEmpty() && driver.findElements(By.id(id)).size() == 1) {
                return "//" + tag + "[@id='" + id + "']";
            }
            if (name != null && !name.isEmpty() && driver.findElements(By.name(name)).size() == 1) {
                return "//" + tag + "[@name='" + name + "']";
            }
            if (placeholder != null && !placeholder.isEmpty() && driver.findElements(By.xpath("//" + tag + "[@placeholder='" + placeholder + "']")).size() == 1) {
                return "//" + tag + "[@placeholder='" + placeholder + "']";
            }
            if (ariaLabel != null && !ariaLabel.isEmpty() && driver.findElements(By.xpath("//" + tag + "[@aria-label='" + ariaLabel + "']")).size() == 1) {
                return "//" + tag + "[@aria-label='" + ariaLabel + "']";
            }
            if (text != null && !text.isEmpty() && driver.findElements(By.xpath("//" + tag + "[contains(text(),'" + text.trim() + "')]")).size() == 1) {
                return "//" + tag + "[contains(text(),'" + text.trim() + "')]";
            }
        }

        return getAbsoluteXPath(driver, elements.get(0));
    }

    public static String getAbsoluteXPath(WebDriver driver, WebElement element) {
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "function getPath(element) {" +
                        "if (element.getAttribute('id') !== null && element.getAttribute('id') !== '') {" +
                        "   return '//' + element.tagName.toLowerCase() + '[@id=\"' + element.getAttribute('id') + '\"]';" +
                        "}" +
                        "if (element === document.body) return '/html/' + element.tagName.toLowerCase();" +
                        "var index = 1, siblings = element.parentNode.childNodes;" +
                        "for (var i = 0; i < siblings.length; i++) {" +
                        "    var sibling = siblings[i];" +
                        "    if (sibling === element) return getPath(element.parentNode) + '/' + element.tagName.toLowerCase() + '[' + index + ']';" +
                        "    if (sibling.nodeType === 1 && sibling.tagName === element.tagName) index++;" +
                        "}" +
                        "return '';" +
                        "}" +
                        "return getPath(arguments[0]);", element);
    }


}
