package com.jb2dev.cv.domain.skills.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SoftSkillTest {

    @Test
    void shouldCreateSoftSkill() {
        // When
        SoftSkill skill = new SoftSkill(1, "Communication");

        // Then
        assertThat(skill.id()).isEqualTo(1);
        assertThat(skill.name()).isEqualTo("Communication");
    }

    @Test
    void shouldCompareEqualSoftSkills() {
        // Given
        SoftSkill skill1 = new SoftSkill(1, "Communication");
        SoftSkill skill2 = new SoftSkill(1, "Communication");

        // Then
        assertThat(skill1).isEqualTo(skill2);
        assertThat(skill1.hashCode()).isEqualTo(skill2.hashCode());
    }

    @Test
    void shouldCompareDifferentSoftSkills() {
        // Given
        SoftSkill skill1 = new SoftSkill(1, "Communication");
        SoftSkill skill2 = new SoftSkill(2, "Teamwork");

        // Then
        assertThat(skill1).isNotEqualTo(skill2);
    }

    @Test
    void shouldHaveToStringMethod() {
        // Given
        SoftSkill skill = new SoftSkill(1, "Communication");

        // When
        String result = skill.toString();

        // Then
        assertThat(result).contains("Communication");
        assertThat(result).contains("1");
    }
}
