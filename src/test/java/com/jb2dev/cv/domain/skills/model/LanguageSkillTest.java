package com.jb2dev.cv.domain.skills.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LanguageSkillTest {

    @Test
    void shouldCreateLanguageSkill() {
        // When
        LanguageSkill skill = new LanguageSkill(
                1,
                "English",
                "C2",
                "C2",
                "C1",
                "C1",
                "C2"
        );

        // Then
        assertThat(skill.id()).isEqualTo(1);
        assertThat(skill.language()).isEqualTo("English");
        assertThat(skill.listening()).isEqualTo("C2");
        assertThat(skill.reading()).isEqualTo("C2");
        assertThat(skill.spokenProduction()).isEqualTo("C1");
        assertThat(skill.spokenInteraction()).isEqualTo("C1");
        assertThat(skill.writing()).isEqualTo("C2");
    }

    @Test
    void shouldCompareEqualLanguageSkills() {
        // Given
        LanguageSkill skill1 = new LanguageSkill(1, "English", "C2", "C2", "C1", "C1", "C2");
        LanguageSkill skill2 = new LanguageSkill(1, "English", "C2", "C2", "C1", "C1", "C2");

        // Then
        assertThat(skill1).isEqualTo(skill2);
        assertThat(skill1.hashCode()).isEqualTo(skill2.hashCode());
    }

    @Test
    void shouldCompareDifferentLanguageSkills() {
        // Given
        LanguageSkill skill1 = new LanguageSkill(1, "English", "C2", "C2", "C1", "C1", "C2");
        LanguageSkill skill2 = new LanguageSkill(2, "Spanish", "C2", "C2", "C2", "C2", "C2");

        // Then
        assertThat(skill1).isNotEqualTo(skill2);
    }

    @Test
    void shouldHaveToStringMethod() {
        // Given
        LanguageSkill skill = new LanguageSkill(1, "English", "C2", "C2", "C1", "C1", "C2");

        // When
        String result = skill.toString();

        // Then
        assertThat(result).contains("English");
        assertThat(result).contains("C2");
        assertThat(result).contains("C1");
    }
}
