package com.Enums;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public enum StepKeyword {
    NAVIGATE("navigate"),
    CLICK("click"),
    ENTER_TEXT("enter"),
    VERIFY_TEXT("should see"),
    CHECKBOX("check"),
    SELECT_DROPDOWN("select"),
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

        // Correct spelling mistakes using LanguageTool
        try {
            bddStep = correctSpelling(bddStep);
        } catch (IOException e) {
            System.err.println("Error in spell checking: " + e.getMessage());
        }

        // Remove extra spaces
        bddStep = bddStep.replaceAll("\\s+", " ");

        // Normalize repeated characters (e.g., "cliiick" → "click")
        bddStep = normalizeSpellingMistakes(bddStep);

        // Apply stemming
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

        // Match the keyword
        String finalBddStep = bddStep;
        return Arrays.stream(values())
                .filter(keyword -> !keyword.getPattern().isEmpty())
                .filter(keyword -> {
                    // Stem the keyword
                    stemmer.setCurrent(keyword.getPattern().toLowerCase());
                    stemmer.stem();
                    String stemmedKeyword = stemmer.getCurrent();

                    // Check if it matches
                    return finalBddStep.contains(stemmedKeyword);
                })
                .findFirst()
                .orElse(UNKNOWN);
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
        return input.replaceAll("(.)\\1*", "$1");  // This will remove all instances of repeated characters
    }
}
