package com.jb2dev.cv.domain;

public enum Language {
    ES_ES("es_ES"),
    EN_EN("en_EN");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Language fromCode(String code) {
        for (Language lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("Invalid language code: " + code);
    }
}
