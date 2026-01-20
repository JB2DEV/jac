package com.jb2dev.cv.domain.skills.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TechnicalSkillCategory {

    LANGUAGE(1, "Language"),
    FRAMEWORK(2, "Framework"),
    DEVOPS(3, "DevOps"),
    CICD(4, "CI/CD"),
    API(5, "API"),
    VCS(6, "VCS"),
    METHODOLOGY(7, "Methodology"),
    DATABASE(8, "Database"),
    TESTING(9, "Testing"),
    CODE_QUALITY(10, "Code Quality"),
    ARCHITECTURE(11, "Architecture"),
    DEVELOPMENT(12, "Development"),
    STREAMING(13, "Streaming"),
    AUTOMATION(14, "Automation"),
    PLATFORM(15, "Platform"),
    DATA(16, "Data");

    private final int id;
    private final String label;

    TechnicalSkillCategory(int id, String label) {
        this.id = id;
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static TechnicalSkillCategory fromJson(String value) {
        for (TechnicalSkillCategory category : values()) {
            if (category.label.equalsIgnoreCase(value)
                    || category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown TechnicalSkillCategory: " + value);
    }

    public static TechnicalSkillCategory fromId(int id) {
        for (TechnicalSkillCategory category : values()) {
            if (category.id == id) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown TechnicalSkillCategory id: " + id);
    }
}
