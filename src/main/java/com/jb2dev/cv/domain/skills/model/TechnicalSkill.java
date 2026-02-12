package com.jb2dev.cv.domain.skills.model;

public record TechnicalSkill(
    int id,
    String name,
    TechnicalSkillCategory category,
    int skillExperience
) {}
