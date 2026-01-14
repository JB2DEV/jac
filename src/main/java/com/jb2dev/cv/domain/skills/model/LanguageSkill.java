package com.jb2dev.cv.domain.skills.model;

public record LanguageSkill(
    int id,
    String language,
    String listening,
    String reading,
    String spokenProduction,
    String spokenInteraction,
    String writing
) {}
