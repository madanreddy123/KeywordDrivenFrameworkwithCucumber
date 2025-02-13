package com.Enums;

public enum StepKeyword {
    NAVIGATE("navigate to"),
    CLICK("click on"),
    ENTER_TEXT("enter"),
    VERIFY_TEXT("should see"),
    CHECKBOX("option"),
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
        bddStep = bddStep.toLowerCase();

        for (StepKeyword keyword : values()) {
            if (!keyword.getPattern().isEmpty() && bddStep.contains(keyword.getPattern())) {
                System.out.printf("Keyword which has been used from the step is: \"%s\"%n", keyword.getPattern());
                return keyword;
            }
        }
        return UNKNOWN;
    }
}
