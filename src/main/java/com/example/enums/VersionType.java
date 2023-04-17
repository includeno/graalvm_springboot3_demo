package com.example.enums;

public enum VersionType {
    PARAGRAPH("paragraph", "段落"),
    SECTION("section", "章节");

    private final String code;
    private final String description;

    VersionType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
