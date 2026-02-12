package com.jb2dev.cv.domain.skills.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TechnicalSkillTest {

    @Test
    void shouldCreateTechnicalSkill() {
        // When
        TechnicalSkill skill = new TechnicalSkill(
                1,
                "Java",
                TechnicalSkillCategory.LANGUAGE,
                5
        );

        // Then
        assertThat(skill.id()).isEqualTo(1);
        assertThat(skill.name()).isEqualTo("Java");
        assertThat(skill.category()).isEqualTo(TechnicalSkillCategory.LANGUAGE);
        assertThat(skill.skillExperience()).isEqualTo(5);
    }

    @Test
    void shouldCompareEqualTechnicalSkills() {
        // Given
        TechnicalSkill skill1 = new TechnicalSkill(
                1,
                "Java",
                TechnicalSkillCategory.LANGUAGE,
                5
        );
        TechnicalSkill skill2 = new TechnicalSkill(
                1,
                "Java",
                TechnicalSkillCategory.LANGUAGE,
                5
        );

        // Then
        assertThat(skill1).isEqualTo(skill2);
        assertThat(skill1.hashCode()).isEqualTo(skill2.hashCode());
    }

    @Test
    void shouldCompareDifferentTechnicalSkills() {
        // Given
        TechnicalSkill skill1 = new TechnicalSkill(
                1,
                "Java",
                TechnicalSkillCategory.LANGUAGE,
                5
        );
        TechnicalSkill skill2 = new TechnicalSkill(
                2,
                "Spring",
                TechnicalSkillCategory.FRAMEWORK,
                4
        );

        // Then
        assertThat(skill1).isNotEqualTo(skill2);
    }

    @Test
    void shouldHaveToStringMethod() {
        // Given
        TechnicalSkill skill = new TechnicalSkill(
                1,
                "Java",
                TechnicalSkillCategory.LANGUAGE,
                5
        );

        // When
        String result = skill.toString();

        // Then
        assertThat(result).contains("Java");
        assertThat(result).contains("LANGUAGE");
    }
}
