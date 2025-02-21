package com.Enums;

public enum Tag {
    INPUT("input"),
    A("a"),
    DIV("div"),
    BUTTON("button");

    private String tagValue;

    Tag(String tagValue) {
        this.tagValue = tagValue;
    }

    public String getTagValue() {
        return tagValue;
    }

    public static Tag fromString(String tagValue) {
        // Debug: Print the value before processing
        System.out.println("Processing tag: '" + tagValue + "'");

        for (Tag tag : Tag.values()) {
            if (tag.getTagValue().equalsIgnoreCase(tagValue.trim())) {
                return tag;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + tagValue);
    }

}
