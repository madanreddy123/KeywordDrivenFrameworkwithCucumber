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

    public String generateDynamicXPathforinput(String fieldText, List<Tag> tags) {
        String searchText = fieldText.trim();
        if (searchText.isEmpty()) {
            System.err.println("🚨 The search text from the BDD step is empty: " + fieldText);
            return "";
        }

        StringBuilder xpathBuilder = new StringBuilder();

        // Loop through the tags
        for (Tag tag : tags) {
            // Start building XPath with a position-based filter
            xpathBuilder.append("//").append(tag.getTagValue()).append("[" +
                    "contains(translate(@placeholder, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "')]" +
                    "[last()]").append(" | ");
        }

        // Fallback XPath based on class and id with position filtering
        xpathBuilder.append("//").append(tags.get(0).getTagValue()).append("[contains(@class, '" + searchText + "') or contains(@id, '" + searchText + "')]").append("[last()]");

        String finalXPath = xpathBuilder.toString().trim();
        if (finalXPath.isEmpty()) {
            System.err.println("🚨 Generated XPath is empty.");
        }

       // System.out.println("Generated XPath: " + finalXPath);
        return finalXPath;
    }

    public String generateDynamicXPathforclick(String fieldText, List<Tag> tags) {
        String searchText = fieldText.trim();
        if (searchText.isEmpty()) {
            System.err.println("🚨 The search text from the BDD step is empty: " + fieldText);
            return "";
        }

        StringBuilder xpathBuilder = new StringBuilder();

        // Loop through the tags
        for (Tag tag : tags) {
            // Start building XPath with a position-based filter
            xpathBuilder.append("//").append(tag.getTagValue()).append("[" +
                    "contains(translate(@placeholder, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "')]" +
                    "[last()]").append(" | ");
        }

        xpathBuilder.append("//button[contains(text(), '" + searchText + "')]").append("[last()]").append(" | ");

        // Fallback XPath based on class and id with position filtering
        xpathBuilder.append("//").append(tags.get(0).getTagValue()).append("[contains(@class, '" + searchText + "') or contains(@id, '" + searchText + "')]").append("[last()]");

        String finalXPath = xpathBuilder.toString().trim();
        if (finalXPath.isEmpty()) {
            System.err.println("🚨 Generated XPath is empty.");
        }

        //System.out.println("Generated XPath: " + finalXPath);
        return finalXPath;
    }



    public String generateDynamiccheckXPath(String fieldText, List<Tag> tags) {
        String searchText = fieldText.trim();
        if (searchText.isEmpty()) {
            System.err.println("🚨 The search text from the BDD step is empty: " + fieldText);
            return "";
        }

        StringBuilder xpathBuilder = new StringBuilder();

        // Loop through the tags (keeping this for general tag-based XPath generation)
        for (Tag tag : tags) {
            String tagValue = tag.getTagValue();
            System.out.println("Processing tag: " + tagValue);  // Log the tag

            // Construct the XPath for different attributes
            String[] attributes = {"placeholder", "title", "aria-label", "name", "text()"};

            for (String attribute : attributes) {
                String attributeCondition = String.format(
                        "contains(translate(@%s, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')",
                        attribute,
                        searchText.toLowerCase()
                );
                xpathBuilder.append("//").append(tagValue).append("[").append(attributeCondition).append("]").append("[last()]").append(" | ");
               // System.out.println("Using attribute: " + attribute + " with condition: " + attributeCondition);  // Log the attribute and condition
            }
        }

        // XPath to locate the input element (radio button) with the target text
        String dynamicPositionXPath =
                "(//*[contains(@type, 'checkbox') and following::text()[contains(., '" + searchText + "')]])[last()]"; // Adjusting for second radio button

        // Append the dynamic position XPath to the generated XPath
        xpathBuilder.append(dynamicPositionXPath);

        String finalXPath = xpathBuilder.toString().trim();
        if (finalXPath.isEmpty()) {
            System.err.println("🚨 Generated XPath is empty.");
        }

        //System.out.println("Generated XPath: " + finalXPath);
        return finalXPath;
    }



    public String generateDynamicradiobuttonXPath(String fieldText, List<Tag> tags) {
        String searchText = fieldText.trim();
        if (searchText.isEmpty()) {
            System.err.println("🚨 The search text from the BDD step is empty: " + fieldText);
            return "";
        }

        StringBuilder xpathBuilder = new StringBuilder();

        // Loop through the tags (keeping this for general tag-based XPath generation)
        for (Tag tag : tags) {
            String tagValue = tag.getTagValue();
            System.out.println("Processing tag: " + tagValue);  // Log the tag

            // Construct the XPath for different attributes
            String[] attributes = {"placeholder", "title", "aria-label", "name", "text()"};

            for (String attribute : attributes) {
                String attributeCondition = String.format(
                        "contains(translate(@%s, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')",
                        attribute,
                        searchText.toLowerCase()
                );
                xpathBuilder.append("//").append(tagValue).append("[").append(attributeCondition).append("]").append("[last()]").append(" | ");
               // System.out.println("Using attribute: " + attribute + " with condition: " + attributeCondition);  // Log the attribute and condition
            }
        }

        // XPath to locate the input element (radio button) with the target text
        String dynamicPositionXPath =
                "(//*[contains(@type, 'radio') and following::text()[contains(., '" + searchText + "')]])[last()]"; // Adjusting for second radio button

        // Append the dynamic position XPath to the generated XPath
        xpathBuilder.append(dynamicPositionXPath);

        String finalXPath = xpathBuilder.toString().trim();
        if (finalXPath.isEmpty()) {
            System.err.println("🚨 Generated XPath is empty.");
        }

        //System.out.println("Generated XPath: " + finalXPath);
        return finalXPath;
    }


    public String generateDynamicselectlassXPath(String fieldText, List<Tag> tags) {
        String searchText = fieldText.trim();
        if (searchText.isEmpty()) {
            System.err.println("🚨 The search text from the BDD step is empty: " + fieldText);
            return "";
        }

        StringBuilder xpathBuilder = new StringBuilder();

        // Loop through the tags (keeping this for general tag-based XPath generation)
        for (Tag tag : tags) {
            String tagValue = tag.getTagValue();
            System.out.println("Processing tag: " + tagValue);  // Log the tag

            // Construct the XPath for different attributes
            String[] attributes = {"placeholder", "title", "aria-label", "name", "text()"};

            for (String attribute : attributes) {
                String attributeCondition = String.format(
                        "contains(translate(@%s, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')",
                        attribute,
                        searchText.toLowerCase()
                );
                xpathBuilder.append("//").append(tagValue).append("[").append(attributeCondition).append("]").append("[last()]").append(" | ");
               // System.out.println("Using attribute: " + attribute + " with condition: " + attributeCondition);  // Log the attribute and condition
            }
        }

        xpathBuilder.append("//select[contains(@name, '" + searchText + "')]").append("[last()]").append(" | ");
        xpathBuilder.append("//select[contains(@id, '" + searchText + "')]").append("[last()]").append(" | ");

        String finalXPath = xpathBuilder.toString().trim();
        if (finalXPath.isEmpty()) {
            System.err.println("🚨 Generated XPath is empty.");
        }

       // System.out.println("Generated XPath: " + finalXPath);
        return finalXPath;
    }



}
