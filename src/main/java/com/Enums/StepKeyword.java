package com.Enums;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum StepKeyword {
    NAVIGATE("navigate"),
    CLICK("click"),
    ENTER_TEXT("enter"),
    VERIFY_TEXT("validate"),
    CHECKBOX("check"),
    SELECT_DROPDOWN("select"),
    CHOOSE_DROPDOWN("Pick"),
    UPLOAD_FILE("upload"),
    CUSTOM_ACTION("generate"),

    UNKNOWN(""); // Default case

    private final String keywordPattern;

    StepKeyword(String keywordPattern) {
        this.keywordPattern = keywordPattern;
    }

    public String getPattern() {
        return keywordPattern;
    }

    public static StepKeyword fromBDDStep(String bddStep) {
        bddStep = bddStep.trim().toLowerCase(); // Normalize case and trim spaces

        // Remove extra spaces and normalize repeated characters
        bddStep = bddStep.replaceAll("\\s+", " ");
        bddStep = normalizeSpellingMistakes(bddStep);

        // First, check if the BDD step directly contains any keyword (exact match)
        for (StepKeyword keyword : values()) {
            if (!keyword.getPattern().isEmpty() && bddStep.contains(keyword.getPattern().toLowerCase())) {
                return keyword;  // Direct match found
            }
        }

        // If no exact match found, apply spelling correction using LanguageTool
        try {
            bddStep = correctSpelling(bddStep);
        } catch (IOException e) {
            System.err.println("Error in spell checking: " + e.getMessage());
        }

        // Normalize repeated characters again after spell correction
        bddStep = normalizeSpellingMistakes(bddStep);

        // Apply stemming to the BDD step to catch variations of words
        EnglishStemmer stemmer = new EnglishStemmer();
        String[] words = bddStep.split(" ");
        words = Arrays.stream(words)
                .map(word -> {
                    stemmer.setCurrent(word);
                    stemmer.stem();
                    return stemmer.getCurrent();
                })
                .toArray(String[]::new);
        bddStep = String.join(" ", words);

        // Finally, check if the stemmed BDD step contains any of the keywords
        for (StepKeyword keyword : values()) {
            if (!keyword.getPattern().isEmpty()) {
                stemmer.setCurrent(keyword.getPattern().toLowerCase());
                stemmer.stem();
                String stemmedKeyword = stemmer.getCurrent();

                if (bddStep.contains(stemmedKeyword)) {
                    return keyword;  // Stemmed match found
                }
            }
        }

        return UNKNOWN;  // Default case if no match found
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

    private static String normalizeSpellingMistakes(String input) {
        return input.replaceAll("(.)\\1*", "$1");  // Normalize repeated characters
    }
}
