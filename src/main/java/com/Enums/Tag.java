package com.Enums;

public enum Tag {
    INPUT("input"),
    TEXTAREA("textarea"),
    A("a"),
    DIV("div"),
    SPAN("span"),
    BUTTON("button"),
    SELECT("select");

    private String tagValue;

    Tag(String tagValue) {
        this.tagValue = tagValue;
    }

    public String getTagValue() {
        return tagValue;
    }

}
