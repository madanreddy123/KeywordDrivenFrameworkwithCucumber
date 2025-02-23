package com.Tagspecifications;

import com.Enums.Tag;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class DynamicLocators {
    WebDriver driver;

    public DynamicLocators(WebDriver driver) {
        this.driver = driver;
    }

    public String generateDynamicXPath(String fieldText, List<Tag> tags) {
        String searchText = fieldText.trim();
        if (searchText.isEmpty()) {
            System.err.println("🚨 The search text from the BDD step is empty: " + fieldText);
            return "";
        }

        StringBuilder xpathBuilder = new StringBuilder();

        // Loop through the tags
        for (Tag tag : tags) {
            xpathBuilder.append("//").append(tag.getTagValue()).append("[" +
                    "contains(translate(@placeholder, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "')]").append(" | ");
        }

        // Include more specific conditions based on the type (e.g., radio, checkbox, etc.)
        xpathBuilder.append("//input[contains(@type, 'radio') and contains(@value, '" + searchText + "')]").append(" | ");
        xpathBuilder.append("//input[contains(@type, 'checkbox') and contains(@value, '" + searchText + "')]").append(" | ");
        xpathBuilder.append("//select[contains(@name, '" + searchText + "')]").append(" | ");
        xpathBuilder.append("//button[contains(text(), '" + searchText + "')]").append(" | ");

        // Fallback XPath based on class and id
        xpathBuilder.append("//").append(tags.get(0).getTagValue()).append("[contains(@class, '" + searchText + "') or contains(@id, '" + searchText + "')]");

        String finalXPath = xpathBuilder.toString().trim();
        if (finalXPath.isEmpty()) {
            System.err.println("🚨 Generated XPath is empty.");
        }

        System.out.println("Generated XPath: " + finalXPath);
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
            xpathBuilder.append("//").append(tag.getTagValue()).append("[" +
                    "contains(translate(@placeholder, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "')]").append(" | ");
        }

        // XPath to get all checkbox input elements
        String baseXPath = "//input[contains(@type, 'checkbox')]";

        // XPath to map the index of the checkbox to the corresponding label text
        xpathBuilder.append(baseXPath).append("[position() = ")
                .append("count(//*[contains(text(), '" + searchText + "')]/preceding::*[contains(@type, 'checkbox')]) + 1")
                .append("]");

        String finalXPath = xpathBuilder.toString().trim();
        if (finalXPath.isEmpty()) {
            System.err.println("🚨 Generated XPath is empty.");
        }

        System.out.println("Generated XPath: " + finalXPath);
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
            xpathBuilder.append("//").append(tag.getTagValue()).append("[" +
                    "contains(translate(@placeholder, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(@name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "') or " +
                    "contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + searchText.toLowerCase() + "')]").append(" | ");
        }

        // XPath to get all checkbox input elements
        String baseXPath = "//input[contains(@type, 'radio')]";

        // XPath to map the index of the checkbox to the corresponding label text
        xpathBuilder.append(baseXPath).append("[position() = ")
                .append("count(//*[contains(text(), '" + searchText + "')]/preceding::*[contains(@type, 'checkbox')]) + 1")
                .append("]");

        String finalXPath = xpathBuilder.toString().trim();
        if (finalXPath.isEmpty()) {
            System.err.println("🚨 Generated XPath is empty.");
        }

        System.out.println("Generated XPath: " + finalXPath);
        return finalXPath;
    }


}
