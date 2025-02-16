package com.utility;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class ExcelBDDReader {

    public void mapToFeatureFile(String filePath, String excelSheetName, String outputFeatureFilePath, String stepDefFolderPath, String newStepDefClassPath) {
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

            // Step 1: Read all classes in the specified folder
            Set<String> existingStepDefinitions = new HashSet<>();
            File stepDefFolder = new File(stepDefFolderPath);

            if (stepDefFolder.exists() && stepDefFolder.isDirectory()) {
                // Get all .java files in the directory (and subdirectories)
                File[] stepDefFiles = stepDefFolder.listFiles((dir, name) -> name.endsWith(".java"));

                if (stepDefFiles != null) {
                    for (File stepDefFile : stepDefFiles) {
                        // Extract method names from each step definition class
                        extractMethodNamesFromClass(stepDefFile, existingStepDefinitions);
                    }
                }
            } else {
                System.out.println("Invalid folder path for step definitions.");
                return;
            }

            // Prepare to update feature file and generate new step definitions
            StringBuilder featureContent = new StringBuilder();
            boolean classStarted = false;

            // Go through each row of Excel data and update the feature and step definition files
            StringBuilder stepDefFileContent = new StringBuilder();
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

                    // Check if the step definition method already exists in any class
                    if (!existingStepDefinitions.contains(methodName.toLowerCase())) {
                        // Add method to the new step definition class content
                        stepDefFileContent.append("\t").append(stepDefMethod).append("\n\n");

                        // Track this method as added
                        existingStepDefinitions.add(methodName.toLowerCase());  // Add to the set of existing methods
                    }
                }
            }

            // Write updated content to the feature file (if any new content is generated)
            if (featureContent.length() > 0) {
                try (FileWriter writer = new FileWriter(featureFile, true)) { // Append mode
                    writer.write(featureContent.toString());
                }
                System.out.println("Feature file updated successfully: " + outputFeatureFilePath);
            } else {
                System.out.println("No new steps to add to the feature file.");
            }

            // Step 2: If there are new step definitions, write them to the new class
            if (stepDefFileContent.length() > 0) {
                File newStepDefFile = new File(newStepDefClassPath);
                if (!newStepDefFile.exists()) {
                    // Create the new file if it doesn't exist
                    newStepDefFile.createNewFile();
                }

                // Read the existing content of the step definition class
                StringBuilder classContent = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(newStepDefFile))) {
                    String line;
                    boolean classBodyStarted = false;
                    boolean classBodyEnded = false;

                    while ((line = reader.readLine()) != null) {
                        classContent.append(line).append("\n");

                        // Check if the class body has started and ended
                        if (line.contains("class")) {
                            classBodyStarted = true;
                        }
                        if (classBodyStarted && line.contains("}")) {
                            classBodyEnded = true;
                            break;  // Stop once we hit the closing brace of the class
                        }
                    }

                    // If class body ended, add the new methods before the closing brace
                    if (classBodyEnded) {
                        int lastIndexOfClassEnd = classContent.lastIndexOf("}");
                        classContent.insert(lastIndexOfClassEnd, stepDefFileContent.toString());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Write the updated class content back to the file
                try (FileWriter writer = new FileWriter(newStepDefFile)) {
                    writer.write(classContent.toString());
                }
                System.out.println("New step definition methods added successfully to: " + newStepDefClassPath);
            } else {
                System.out.println("No new step definitions to add.");
            }

        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    // Method to extract method names from the given step definition class file
    private void extractMethodNamesFromClass(File classFile, Set<String> existingStepDefinitions) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(classFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Use regex to find method names in the form of "public void methodName()"
                Matcher matcher = Pattern.compile("public void (\\w+)\\(\\)").matcher(line);
                if (matcher.find()) {
                    String methodName = matcher.group(1);
                    existingStepDefinitions.add(methodName.toLowerCase()); // Track methods in lowercase for case-insensitive comparison
                }
            }
        }
    }

    // Helper method to generate method name from the BDD step
    private String generateMethodName(String bddStep) {
        String sanitizedStep = bddStep.replaceAll("(?i)^(Given|When|Then|And)\\s*", "").trim();
        sanitizedStep = sanitizedStep.replaceAll("[\"'$#@!^%&*\\[\\]:{}<>,.;|\\s]", "").trim().replaceAll("[^a-zA-Z]", "_").toLowerCase();
        return sanitizedStep;
    }

    // Helper method to generate step definition method based on BDD step
    private String generateStepDefMethod(String bddStep, String methodName) {
        String stepWithoutKeyword = bddStep.replaceAll("(?i)^(Given|When|Then|And)\\s*", "").trim();
        stepWithoutKeyword = stepWithoutKeyword.replaceAll("[\"']", "");
        String annotation = getStepAnnotation(bddStep);
        return String.format(
                "@%s(\"%s\")\n" +
                        "public void %s() {\n" +
                        "}\n\n",
                annotation, stepWithoutKeyword, methodName
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
        }
        else if (bddStep.toLowerCase().startsWith("but")) {
        return "But";
    }
    else {
            return "Given";  // Default to Given if no match
        }
    }
}
