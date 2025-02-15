package com.utility;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.*;
import java.util.List;
import java.util.Map;

public class ExcelBDDReader {

    public void mapToFeatureFile(String filePath, String excelSheetName, String outputFeatureFilePath, String stepDefFilePath) {
        System.out.println("Started updating feature file and step definitions.");

        List<Map<String, String>> excelData;
        try {
            ExcelReader excelReader = new ExcelReader();
            excelData = excelReader.getData(filePath, excelSheetName);

            if (excelData.isEmpty()) {
                System.out.println("No data found in the Excel sheet!");
                return;
            }

            // Read existing feature file content (if any)
            File featureFile = new File(outputFeatureFilePath);
            StringBuilder featureFileContent = new StringBuilder();

            if (featureFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(featureFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        featureFileContent.append(line.trim()).append("\n");
                    }
                }
            }

            // Read existing step definition file content (if any)
            File stepDefFile = new File(stepDefFilePath);
            StringBuilder stepDefFileContent = new StringBuilder();

            if (stepDefFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(stepDefFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stepDefFileContent.append(line.trim()).append("\n");
                    }
                }
            }

            // Find the position of the last '}' (closing brace)
            int lastBraceIndex = stepDefFileContent.lastIndexOf("}");

            // Prepare to update feature file and generate step definitions
            StringBuilder featureContent = new StringBuilder();
            StringBuilder stepDefContent = new StringBuilder();

            boolean classStarted = false;  // Track if the class block is opened

            // Go through each row of Excel data
            for (Map<String, String> rowData : excelData) {
                String scenarioTitle = rowData.get("Scenario");
                String bddStep = rowData.get("BDD Steps");

                if (scenarioTitle != null && !scenarioTitle.isEmpty() && bddStep != null && !bddStep.isEmpty()) {
                    scenarioTitle = scenarioTitle.trim();
                    bddStep = bddStep.trim();
                    bddStep = bddStep.replaceAll("[\"'$#@!^%&*\\[\\]():{}<>,.;|]", "").trim();

                    // Add scenario to feature file (if not already present)
                    String scenarioLine = "Scenario: " + scenarioTitle;
                    if (!featureFileContent.toString().contains(scenarioLine)) {
                        featureContent.append("\n").append(scenarioLine).append("\n");
                    }

                    // Add BDD step to feature file (if not already present)
                    if (!featureFileContent.toString().contains(bddStep)) {
                        featureContent.append(bddStep).append("\n");
                    }

                    // Generate the corresponding step definition method
                    String methodName = generateMethodName(bddStep);
                    String stepDefMethod = generateStepDefMethod(bddStep, methodName);

                    // Check if the step definition method already exists in the step definition file
                    if (!stepDefFileContent.toString().contains(stepDefMethod)) {
                        // Check if class definition is already present
                        if (!classStarted) {
                            // Add class definition if it's not there yet
                            stepDefContent.append("\n\n");
                            classStarted = true;  // Mark class as started
                        }

                        // Add the step definition method inside the class
                        stepDefContent.append("\t").append(stepDefMethod).append("\n\n");
                    }
                }
            }

            // Ensure the last '}' is correctly placed after adding methods
            if (lastBraceIndex != -1) {
                // Insert the new step definitions just before the last '}' in the step definition file
                stepDefFileContent.insert(lastBraceIndex, stepDefContent.toString());
            }

            // Write updated content to the feature file
            if (featureContent.length() > 0) {
                try (FileWriter writer = new FileWriter(featureFile, true)) { // Append mode
                    writer.write(featureContent.toString());
                }
                System.out.println("Feature file updated successfully: " + outputFeatureFilePath);
            } else {
                System.out.println("No new steps to add to the feature file.");
            }

            // Write the updated step definition content to the step definition file
            if (stepDefFileContent.length() > 0) {
                try (FileWriter writer = new FileWriter(stepDefFilePath)) { // Overwrite the file
                    writer.write(stepDefFileContent.toString());
                }
                System.out.println("Step definition methods added successfully to: " + stepDefFilePath);
            } else {
                System.out.println("No new step definitions to add.");
            }

        } catch (InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to generate method name from the BDD step
    private String generateMethodName(String bddStep) {
        // Remove BDD keywords (Given, When, Then, And) and any quotes (single/double)
        String sanitizedStep = bddStep.replaceAll("(?i)^(Given|When|Then|And)\\s*", "").trim();

        // Remove single and double quotes, and any non-alphabetic characters except spaces
        sanitizedStep = sanitizedStep.replaceAll("[\"'$#@!^%&*\\[\\]:{}<>,.;|\\s]", "").trim().replaceAll("[^a-zA-Z]", "_").toLowerCase();

        return sanitizedStep;
    }

    // Helper method to generate step definition method based on BDD step
    private String generateStepDefMethod(String bddStep, String methodName) {
        // Remove BDD keywords (Given, When, Then, And)
        String stepWithoutKeyword = bddStep.replaceAll("(?i)^(Given|When|Then|And)\\s*", "").trim();

        // Remove single and double quotes from the BDD step
        stepWithoutKeyword = stepWithoutKeyword.replaceAll("[\"']", "");

        String annotation = getStepAnnotation(bddStep);
        return String.format(
                "@%s(\"%s\")\n" +
                        "public void %s() {\n" +
                        "}\n\n",
                annotation, stepWithoutKeyword, methodName, bddStep
        );
    }

    // Helper method to determine the correct step annotation
    private String getStepAnnotation(String bddStep) {
        if (bddStep.toLowerCase().startsWith("given")) {
            return "Given";
        } else if (bddStep.toLowerCase().startsWith("when")) {
            return "When";
        } else if (bddStep.toLowerCase().startsWith("then")) {
            return "Then";
        } else if (bddStep.toLowerCase().startsWith("and")) {
            return "And";
        } else {
            return "Given";  // Default to Given if no match
        }
    }
}
