package com.jb2dev.cv.domain.profile.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonalInfoTest {

    @Test
    void shouldCreatePersonalInfo() {
        // When
        PersonalInfo info = new PersonalInfo(
                "John Doe",
                "1990-01-15",
                "American",
                "Male"
        );

        // Then
        assertThat(info.fullName()).isEqualTo("John Doe");
        assertThat(info.birthDate()).isEqualTo("1990-01-15");
        assertThat(info.nationality()).isEqualTo("American");
        assertThat(info.gender()).isEqualTo("Male");
    }

    @Test
    void shouldCompareEqualPersonalInfo() {
        // Given
        PersonalInfo info1 = new PersonalInfo("John Doe", "1990-01-15", "American", "Male");
        PersonalInfo info2 = new PersonalInfo("John Doe", "1990-01-15", "American", "Male");

        // Then
        assertThat(info1).isEqualTo(info2);
        assertThat(info1.hashCode()).isEqualTo(info2.hashCode());
    }

    @Test
    void shouldCompareDifferentPersonalInfo() {
        // Given
        PersonalInfo info1 = new PersonalInfo("John Doe", "1990-01-15", "American", "Male");
        PersonalInfo info2 = new PersonalInfo("Jane Smith", "1992-05-20", "British", "Female");

        // Then
        assertThat(info1).isNotEqualTo(info2);
    }

    @Test
    void shouldHaveToStringMethod() {
        // Given
        PersonalInfo info = new PersonalInfo("John Doe", "1990-01-15", "American", "Male");

        // When
        String result = info.toString();

        // Then
        assertThat(result).contains("John Doe");
        assertThat(result).contains("1990-01-15");
    }
}
