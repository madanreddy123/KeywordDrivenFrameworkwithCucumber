package com.Enums;

import org.tartarus.snowball.ext.EnglishStemmer;

import java.util.Arrays;

public enum StepKeyword {
    NAVIGATE("navigate"),
    CLICK("click to"),
    ENTER_TEXT("enter the"),
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
        bddStep = bddStep.trim().toLowerCase();  // Normalize by trimming and converting to lower case

        // Remove extra spaces between words by replacing multiple spaces with a single space
        bddStep = bddStep.replaceAll("\\s+", " ");

        // Normalize common spelling mistakes (like repeated letters) in BDD step
        bddStep = normalizeSpellingMistakes(bddStep);

        // Create a Snowball Stemmer for English
        EnglishStemmer stemmer = new EnglishStemmer();

        // Apply stemming to BDD step to handle variations
        String[] words = bddStep.split(" ");
        words = Arrays.stream(words)
                .map(word -> {
                    stemmer.setCurrent(word);
                    stemmer.stem();
                    return stemmer.getCurrent();  // Apply stemming to each word
                })
                .toArray(String[]::new);

        // Rebuild the BDD step after stemming
        bddStep = String.join(" ", words);

        // Use stream to check the keyword match
        String finalBddStep = bddStep;
        return Arrays.stream(values())
                .filter(keyword -> !keyword.getPattern().isEmpty())
                .filter(keyword -> {
                    // Get the keyword pattern and stem it
                    stemmer.setCurrent(keyword.getPattern().toLowerCase());
                    stemmer.stem();
                    String stemmedKeyword = stemmer.getCurrent();
                    // Check if the stemmed keyword matches part of the stemmed BDD step
                    return finalBddStep.contains(stemmedKeyword);
                })
                .findFirst()
                .map(keyword -> {
                    System.out.printf("Keyword which has been used from the step is: \"%s\"%n", keyword.getPattern());
                    return keyword;
                })
                .orElse(UNKNOWN);  // If no match found
    }

    private static String normalizeSpellingMistakes(String input) {
        return input.replaceAll("(.)\\1*", "$1");  // This will remove all instances of repeated characters
    }
}
