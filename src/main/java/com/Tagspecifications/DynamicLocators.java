package com.Tagspecifications;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class DynamicLocators {
    WebDriver driver;

    public DynamicLocators(WebDriver driver) {
        this.driver = driver;
    }

    public String generateXPath(WebDriver driver, String fieldName) {
        // Start by building a generalized XPath query using the field name
        String xpathQuery = "//*[contains(text(), '" + fieldName + "') " +
                "or contains(@id, '" + fieldName + "') " +
                "or contains(@name, '" + fieldName + "') " +
                "or contains(@class, '" + fieldName + "') " +
                "or contains(@placeholder, '" + fieldName + "') " +
                "or contains(@aria-label, '" + fieldName + "')]";

        List<WebElement> elements = driver.findElements(By.xpath(xpathQuery));
        if (elements.isEmpty()) return "Field not found";

        for (WebElement element : elements) {
            String xpath = getOptimalXPath(driver, element, fieldName);
            if (xpath != null) return xpath;
        }

        // Fallback to absolute XPath for the first element
        return getAbsoluteXPath(driver, elements.get(0));
    }

    private String getOptimalXPath(WebDriver driver, WebElement element, String fieldName) {
        String tag = element.getTagName();
        String text = element.getText();
        String id = element.getAttribute("id");
        String name = element.getAttribute("name");
        String placeholder = element.getAttribute("placeholder");
        String ariaLabel = element.getAttribute("aria-label");
        String angularAttr = getAngularAttribute(element); // Detect Angular attributes
        String dataAttr = getDataAttribute(element); // Detect data-test attributes

        // 1️⃣ Prioritize exact text match (check if text is not empty or null)
        if (text != null && !text.trim().isEmpty()) {
            String normalizedText = text.trim();
            String xpathText = "normalize-space(text())='" + normalizedText + "'";

            if (isUniqueLocator(driver, By.xpath("//" + tag + "[" + xpathText + "]"))) {
                return "//" + tag + "[" + xpathText + "]";
            }
        }

        // 2️⃣ Prioritize unique Angular attributes
        if (angularAttr != null) {
            String angularValue = element.getAttribute(angularAttr);
            if (angularValue != null && !angularValue.trim().isEmpty() &&
                    isUniqueLocator(driver, By.xpath("//" + tag + "[@" + angularAttr + "='" + angularValue + "']"))) {
                return "//" + tag + "[@" + angularAttr + "='" + angularValue + "']";
            }
        }

        // 3️⃣ Prioritize data-test attributes
        if (dataAttr != null && isUniqueLocator(driver, By.xpath("//" + tag + "[@" + dataAttr + "]"))) {
            return "//" + tag + "[@" + dataAttr + "]";
        }

        // 4️⃣ Prioritize unique id
        if (id != null && !id.trim().isEmpty() && isUniqueLocator(driver, By.id(id))) {
            return "//" + tag + "[@id='" + id + "']";
        }

        // 5️⃣ Handle name, placeholder, and aria-label attributes
        if (name != null && !name.isEmpty() && isUniqueLocator(driver, By.xpath("//" + tag + "[@name='" + name + "']"))) {
            return "//" + tag + "[@name='" + name + "']";
        }

        if (placeholder != null && !placeholder.isEmpty() && isUniqueLocator(driver, By.xpath("//" + tag + "[@placeholder='" + placeholder + "']"))) {
            return "//" + tag + "[@placeholder='" + placeholder + "']";
        }

        if (ariaLabel != null && !ariaLabel.isEmpty() && isUniqueLocator(driver, By.xpath("//" + tag + "[@aria-label='" + ariaLabel + "']"))) {
            return "//" + tag + "[@aria-label='" + ariaLabel + "']";
        }

        // 6️⃣ Fallback to text match if no other criteria are met
        if (text != null && !text.isEmpty() && isUniqueLocator(driver, By.xpath("//" + tag + "[contains(text(),'" + text.trim() + "')]"))) {
            return "//" + tag + "[contains(text(),'" + text.trim() + "')]";
        }

        return null;  // If no optimal XPath was found
    }

    private boolean isUniqueLocator(WebDriver driver, By locator) {
        if (locator == null) return false;
        try {
            List<WebElement> elements = driver.findElements(locator);
            return elements.size() == 1;
        } catch (org.openqa.selenium.InvalidSelectorException e) {
            return false;  // Prevent XPath syntax issues
        }
    }

    private String getDataAttribute(WebElement element) {
        // Look for data-* attributes in the element's HTML
        for (String attribute : element.getAttribute("outerHTML").split(" ")) {
            if (attribute.startsWith("data-")) {
                return attribute.split("=")[0];
            }
        }
        return null;
    }

    private String getAngularAttribute(WebElement element) {
        String outerHTML = element.getAttribute("outerHTML");

        // List of common Angular attributes
        String[] angularAttributes = {"ng-model", "ng-bind", "ng-click", "ng-class", "ng-reflect-", "formControlName"};

        for (String attr : angularAttributes) {
            if (outerHTML.contains(attr + "=")) {
                return attr;
            }
        }
        return null; // Return null if no Angular attribute is found
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
