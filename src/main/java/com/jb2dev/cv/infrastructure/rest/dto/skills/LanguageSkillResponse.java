package com.jb2dev.cv.infrastructure.rest.dto.skills;

public record LanguageSkillResponse(
    int id,
    String language,
    String listening,
    String reading,
    String spokenProduction,
    String spokenInteraction,
    String writing
) {}
