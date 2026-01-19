package com.jb2dev.cv.domain.skills.model;

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

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static TechnicalSkillCategory fromId(int id) {
        for (TechnicalSkillCategory category : values()) {
            if (category.id == id) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown TechnicalSkillCategory id: " + id);
    }

    public static TechnicalSkillCategory fromLabel(String label) {
        for (TechnicalSkillCategory category : values()) {
            if (category.label.equalsIgnoreCase(label)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown TechnicalSkillCategory label: " + label);
    }
}
