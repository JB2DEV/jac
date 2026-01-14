package com.jb2dev.cv.infrastructure.rest.dto.skills;

public record TechnicalSkillResponse(
    int id,
    String name,
    String category,
    int skillExperience
) {}
