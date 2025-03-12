package com.Enums;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

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

    // Maps for efficient lookups
    private static final Map<String, StepKeyword> keywordMap = new HashMap<>();
    private static final Map<Pattern, StepKeyword> regexKeywordMap = new HashMap<>();

    // ThreadLocal for thread-safe spell-checking
    private static final ThreadLocal<JLanguageTool> threadLocalLangTool = ThreadLocal.withInitial(() -> {
        try {
            return new JLanguageTool(new AmericanEnglish());
        } catch (Exception e) {
            System.err.println("Error initializing LanguageTool: " + e.getMessage());
            return null;
        }
    });

    // Static block for initializing keyword maps
    static {
        for (StepKeyword keyword : values()) {
            for (String pattern : keyword.getPatterns()) {
                keywordMap.put(pattern.toLowerCase(), keyword);
                regexKeywordMap.put(Pattern.compile("\\b" + Pattern.quote(pattern) + "\\b", Pattern.CASE_INSENSITIVE), keyword);
            }
        }
    }

    // Private constructor ensuring immutability
    StepKeyword(String... keywordPatterns) {
        this.keywordPatterns = Collections.unmodifiableList(Arrays.asList(keywordPatterns));
    }

    public List<String> getPatterns() {
        return keywordPatterns;
    }

    /**
     * Identifies the StepKeyword from a given BDD step string.
     *
     * @param bddStep The step text to classify.
     * @return Corresponding StepKeyword enum value.
     */
    public static StepKeyword fromBDDStep(String bddStep) {
        String normalizedStep = bddStep.trim().toLowerCase();

        // Try direct keyword map lookup
        for (String key : keywordMap.keySet()) {
            if (normalizedStep.contains(key)) {
                return keywordMap.get(key);
            }
        }

        // Try regex-based keyword matching
        for (Map.Entry<Pattern, StepKeyword> entry : regexKeywordMap.entrySet()) {
            if (entry.getKey().matcher(normalizedStep).find()) {
                return entry.getValue();
            }
        }

        // Apply spell checking if needed
        String correctedStep = correctSpellingIfPossible(normalizedStep);

        // Check again after correction
        for (String key : keywordMap.keySet()) {
            if (correctedStep.contains(key)) {
                return keywordMap.get(key);
            }
        }

        for (Map.Entry<Pattern, StepKeyword> entry : regexKeywordMap.entrySet()) {
            if (entry.getKey().matcher(correctedStep).find()) {
                return entry.getValue();
            }
        }

        return UNKNOWN;
    }

    /**
     * Applies spelling correction if the language tool is available.
     *
     * @param input The text to check.
     * @return Corrected string if applicable, otherwise the original.
     */
    private static String correctSpellingIfPossible(String input) {
        JLanguageTool tool = threadLocalLangTool.get();
        if (tool == null) return input;

        try {
            return correctSpelling(input, tool);
        } catch (IOException e) {
            System.err.println("Error in spell checking: " + e.getMessage());
            return input;
        }
    }

    /**
     * Corrects spelling mistakes in a given string using LanguageTool.
     *
     * @param input The text to check.
     * @param tool  The spell checker instance.
     * @return Corrected string.
     * @throws IOException If an error occurs during spell checking.
     */
    private static String correctSpelling(String input, JLanguageTool tool) throws IOException {
        List<RuleMatch> matches = tool.check(input);
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
}