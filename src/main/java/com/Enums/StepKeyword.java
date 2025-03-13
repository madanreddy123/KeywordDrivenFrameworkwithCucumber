package com.Enums;

import java.util.*;
import java.util.logging.Logger;
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
    UNKNOWN();

    private final List<String> keywordPatterns;
    private static final Map<String, StepKeyword> keywordMap = new HashMap<>();
    private static final Map<Pattern, StepKeyword> regexKeywordMap = new HashMap<>();
    private static final Map<StepKeyword, Integer> keywordPriority = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(StepKeyword.class.getName());

    static {
        int priority = 10;
        for (StepKeyword keyword : values()) {
            for (String pattern : keyword.getPatterns()) {
                keywordMap.put(pattern.toLowerCase(), keyword);
                regexKeywordMap.put(Pattern.compile("\\b" + Pattern.quote(pattern) + "\\b", Pattern.CASE_INSENSITIVE), keyword);
            }
            keywordPriority.put(keyword, priority--);
        }
    }

    StepKeyword(String... keywordPatterns) {
        this.keywordPatterns = Collections.unmodifiableList(Arrays.asList(keywordPatterns));
    }

    public List<String> getPatterns() {
        return keywordPatterns;
    }

    public static StepKeyword fromBDDStep(String bddStep) {
        String normalizedStep = bddStep.trim().toLowerCase();

        // Extract only words outside of quotes
        StringBuilder cleanedStep = new StringBuilder();
        boolean insideQuotes = false;
        char prevChar = 0;

        // Iterate over the characters to remove quoted content but keep unquoted ones
        for (char c : normalizedStep.toCharArray()) {
            if (c == '"' || c == '\'') {
                // Handle quote toggle
                if (prevChar != '\\') {
                    insideQuotes = !insideQuotes;
                    continue; // Skip the quote itself
                }
            }
            if (!insideQuotes) {
                cleanedStep.append(c); // Add the character only if it's outside quotes
            }
            prevChar = c;
        }

        // Now process the unquoted cleaned step
        List<String> words = Arrays.asList(cleanedStep.toString().trim().split("\\s+"));
        PriorityQueue<StepKeyword> matchedKeywords = new PriorityQueue<>(
                Comparator.comparingInt(keywordPriority::get).reversed()
        );

        // Match using keywordMap
        for (String key : keywordMap.keySet()) {
            if (words.contains(key)) {
                matchedKeywords.add(keywordMap.get(key));
            }
        }

        // Match using regex patterns
        for (Map.Entry<Pattern, StepKeyword> entry : regexKeywordMap.entrySet()) {
            if (entry.getKey().matcher(cleanedStep.toString()).find()) {
                matchedKeywords.add(entry.getValue());
            }
        }

        return matchedKeywords.isEmpty() ? UNKNOWN : matchedKeywords.poll();
    }
}