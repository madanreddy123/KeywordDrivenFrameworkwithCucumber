package com.Enums;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.util.*;

public enum StepKeyword {
    NAVIGATE("navigate", "go", "open"),
    CLICK("click", "press", "tap"),
    ENTER_TEXT("enter", "type", "write"),
    VERIFY_TEXT("validate", "verify", "assert"),
    CHECKBOX("check", "tick", "mark"),
    SELECT_DROPDOWN("select", "choose", "pick"),
    UPLOAD_FILE("upload", "attach", "add"),
    CUSTOM_ACTION("generate", "create", "build"),
    UNKNOWN(); // Default case

    private final List<String> keywordPatterns;

    StepKeyword(String... keywordPatterns) {
        this.keywordPatterns = Arrays.asList(keywordPatterns);
    }

    public List<String> getPatterns() {
        return keywordPatterns;
    }

    public static StepKeyword fromBDDStep(String bddStep) {
        bddStep = bddStep.trim().toLowerCase();

        // Check for direct keyword matches
        for (StepKeyword keyword : values()) {
            for (String pattern : keyword.getPatterns()) {
                if (!pattern.isEmpty() && bddStep.contains(pattern.toLowerCase())) {
                    return keyword;  // Direct match found
                }
            }
        }

        // Apply spelling correction using LanguageTool
        try {
            bddStep = correctSpelling(bddStep);
        } catch (IOException e) {
            System.err.println("Error in spell checking: " + e.getMessage());
        }

        // Check again after spell correction
        for (StepKeyword keyword : values()) {
            for (String pattern : keyword.getPatterns()) {
                if (!pattern.isEmpty() && bddStep.contains(pattern.toLowerCase())) {
                    return keyword;  // Match after spell correction
                }
            }
        }

        return UNKNOWN;
    }

    // Method to correct spelling errors using LanguageTool
    private static String correctSpelling(String input) throws IOException {
        JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
        List<RuleMatch> matches = langTool.check(input);

        StringBuilder correctedText = new StringBuilder(input);
        int offset = 0;

        for (RuleMatch match : matches) {
            List<String> suggestions = match.getSuggestedReplacements();
            if (!suggestions.isEmpty()) {
                String suggestion = suggestions.get(0); // Take the first suggestion
                int start = match.getFromPos() + offset;
                int end = match.getToPos() + offset;
                correctedText.replace(start, end, suggestion);
                offset += suggestion.length() - (end - start);
            }
        }

        return correctedText.toString();
    }
}
