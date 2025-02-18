package com.utility;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class ExcelBDDReadertwo {

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

            Set<String> existingStepDefinitions = new HashSet<>();
            File stepDefFolder = new File(stepDefFolderPath);

            if (stepDefFolder.exists() && stepDefFolder.isDirectory()) {
                File[] stepDefFiles = stepDefFolder.listFiles((dir, name) -> name.endsWith(".java"));

                if (stepDefFiles != null) {
                    for (File stepDefFile : stepDefFiles) {
                        extractMethodNamesFromClass(stepDefFile, existingStepDefinitions);
                    }
                }
            } else {
                System.out.println("Invalid folder path for step definitions.");
                return;
            }

            StringBuilder featureContent = new StringBuilder();
            StringBuilder stepDefFileContent = new StringBuilder();
            String previousStep = null;

            for (Map<String, String> rowData : excelData) {
                String scenarioTitle = rowData.get("Scenario");
                String bddStep = rowData.get("BDD Steps");

                if (scenarioTitle != null && !scenarioTitle.isEmpty() && bddStep != null && !bddStep.isEmpty()) {
                    scenarioTitle = scenarioTitle.trim();
                    bddStep = correctGrammar(bddStep.trim());
                    bddStep = bddStep.replaceAll("[\"'$#@!^%&*\\[\\]():{}<>,.;|]", "").trim();

                    String formattedStep = inferBDDKeyword(bddStep, previousStep);
                    previousStep = formattedStep;

                    String scenarioLine = "Scenario: " + scenarioTitle;
                    if (!featureFileContent.toString().contains(scenarioLine)) {
                        featureContent.append("\n").append(scenarioLine).append("\n");
                    }

                    if (!featureFileContent.toString().contains(formattedStep)) {
                        featureContent.append(formattedStep).append("\n");
                    }

                    String methodName = generateMethodName(bddStep);
                    String stepDefMethod = generateStepDefMethod(formattedStep, methodName);

                    if (!existingStepDefinitions.contains(methodName.toLowerCase())) {
                        stepDefFileContent.append("\t").append(stepDefMethod).append("\n\n");
                        existingStepDefinitions.add(methodName.toLowerCase());
                    }
                }
            }

            if (featureContent.length() > 0) {
                try (FileWriter writer = new FileWriter(featureFile, true)) {
                    writer.write(featureContent.toString());
                }
                System.out.println("Feature file updated successfully: " + outputFeatureFilePath);
            }

            if (stepDefFileContent.length() > 0) {
                File newStepDefFile = new File(newStepDefClassPath);
                if (!newStepDefFile.exists()) {
                    newStepDefFile.createNewFile();
                }

                StringBuilder classContent = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(newStepDefFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        classContent.append(line).append("\n");
                    }
                }

                int lastIndexOfClassEnd = classContent.lastIndexOf("}");
                if (lastIndexOfClassEnd != -1) {
                    classContent.insert(lastIndexOfClassEnd, stepDefFileContent.toString());
                }

                try (FileWriter writer = new FileWriter(newStepDefFile)) {
                    writer.write(classContent.toString());
                }
                System.out.println("New step definition methods added successfully to: " + newStepDefClassPath);
            }

        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    private String inferBDDKeyword(String step, String previousStep) {
        String trimmedStep = step.trim().toLowerCase();

        // Check if the step already starts with a Gherkin keyword
        if (trimmedStep.startsWith("given ") ||
                trimmedStep.startsWith("when ") ||
                trimmedStep.startsWith("then ") ||
                trimmedStep.startsWith("and ")) {
            return step; // Return as is, without modifying
        }

        // Infer the appropriate keyword if missing
        if (previousStep == null || previousStep.isEmpty()) {
            return "Given " + step;
        } else if (previousStep.startsWith("Given")) {
            return "When " + step;
        } else if (previousStep.startsWith("When") || previousStep.startsWith("And When")) {
            return "Then " + step;
        } else {
            return "And " + step;
        }
    }


    private String correctGrammar(String text) {
        try {
            JLanguageTool langTool = new JLanguageTool(new BritishEnglish());
            List<RuleMatch> matches = langTool.check(text);

            StringBuilder correctedText = new StringBuilder(text);
            ListIterator<RuleMatch> iterator = matches.listIterator(matches.size());

            while (iterator.hasPrevious()) {
                RuleMatch match = iterator.previous();
                List<String> suggestions = match.getSuggestedReplacements();
                if (!suggestions.isEmpty()) {
                    correctedText.replace(match.getFromPos(), match.getToPos(), suggestions.get(0));
                }
            }

            return correctedText.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    private void extractMethodNamesFromClass(File classFile, Set<String> existingStepDefinitions) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(classFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = Pattern.compile("public void (\\w+)\\(\\)").matcher(line);
                if (matcher.find()) {
                    existingStepDefinitions.add(matcher.group(1).toLowerCase());
                }
            }
        }
    }

    private String generateMethodName(String bddStep) {
        return bddStep.replaceAll("(?i)^(Given|When|Then|And)\\s*", "")
                .replaceAll("[^a-zA-Z]", "_")
                .toLowerCase();
    }

    private String generateStepDefMethod(String bddStep, String methodName) {
        String stepWithoutKeyword = bddStep.replaceAll("(?i)^(Given|When|Then|And)\\s*", "").trim();
        String annotation = getStepAnnotation(bddStep);

        return String.format("@%s(\"%s\")\npublic void %s() {\n    // Step implementation\n}\n",
                annotation, stepWithoutKeyword, methodName);
    }

    private String getStepAnnotation(String bddStep) {
        if (bddStep.toLowerCase().startsWith("given")) return "Given";
        if (bddStep.toLowerCase().startsWith("when")) return "When";
        if (bddStep.toLowerCase().startsWith("then")) return "Then";
        if (bddStep.toLowerCase().startsWith("and")) return "And";
        return "Given";
    }
}
