package com.Enums;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public enum StepKeyword {
    NAVIGATE("navigate", "go", "move"),
    CLICK("click", "press", "tap"),
    ENTER_TEXT("enter", "input", "type"),
    VERIFY_TEXT("validate", "confirm", "assert"),
    CHECKBOX("select checkbox", "tick checkbox", "mark box"),
    SELECT_DROPDOWN("select option", "choose option", "pick from list"),
    CHOOSE_DROPDOWN("pick dropdown", "choose dropdown"),
    UPLOAD_FILE("upload", "attach", "submit file"),
    CUSTOM_ACTION("generate", "create", "produce"),

    UNKNOWN(""); // Default case

    private final List<String> keywordPatterns;

    StepKeyword(String... keywordPatterns) {
        this.keywordPatterns = Arrays.asList(keywordPatterns);
    }

    public List<String> getPatterns() {
        return keywordPatterns;
    }

    public static StepKeyword fromBDDStep(String bddStep) {
        bddStep = bddStep.trim().toLowerCase();
        bddStep = bddStep.replaceAll("\\s+", " ");
        bddStep = normalizeSpellingMistakes(bddStep);

        // Check if any keyword pattern is directly present in the BDD step
        for (StepKeyword keyword : values()) {
            for (String pattern : keyword.getPatterns()) {
                if (!pattern.isEmpty() && bddStep.contains(pattern.toLowerCase())) {
                    return keyword;
                }
            }
        }

        // Apply spell correction using LanguageTool
        try {
            bddStep = correctSpelling(bddStep);
        } catch (IOException e) {
            System.err.println("Error in spell checking: " + e.getMessage());
        }

        bddStep = normalizeSpellingMistakes(bddStep);

        // Apply stemming to BDD step
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

        // Check again after stemming
        for (StepKeyword keyword : values()) {
            for (String pattern : keyword.getPatterns()) {
                if (!pattern.isEmpty()) {
                    stemmer.setCurrent(pattern.toLowerCase());
                    stemmer.stem();
                    String stemmedKeyword = stemmer.getCurrent();

                    if (bddStep.contains(stemmedKeyword)) {
                        return keyword;
                    }
                }
            }
        }

        return UNKNOWN;
    }

    private static String correctSpelling(String input) throws IOException {
        JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
        List<RuleMatch> matches = langTool.check(input);

        StringBuilder correctedText = new StringBuilder(input);
        int offset = 0;

        for (RuleMatch match : matches) {
            List<String> suggestions = match.getSuggestedReplacements();
            if (!suggestions.isEmpty()) {
                String suggestion = suggestions.get(0);
                int start = match.getFromPos() + offset;
                int end = match.getToPos() + offset;
                correctedText.replace(start, end, suggestion);
                offset += suggestion.length() - (end - start);
            }
        }

        return correctedText.toString();
    }

    private static String normalizeSpellingMistakes(String input) {
        return input.replaceAll("(.)\\1+", "$1");  // Normalize repeated characters
    }
}