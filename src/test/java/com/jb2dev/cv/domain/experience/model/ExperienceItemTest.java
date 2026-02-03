package com.jb2dev.cv.domain.experience.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ExperienceItemTest {

    @Test
    void shouldCreateExperienceItem() {
        // Given
        LocalDate startDate = LocalDate.of(2020, 1, 15);
        LocalDate endDate = LocalDate.of(2023, 6, 30);

        // When
        ExperienceItem item = new ExperienceItem(
                1,
                "Senior Java Developer",
                "Tech Corp",
                "New York, NY",
                startDate,
                endDate,
                false,
                "Led development team",
                "Developed microservices using Spring Boot"
        );

        // Then
        assertThat(item.id()).isEqualTo(1);
        assertThat(item.role()).isEqualTo("Senior Java Developer");
        assertThat(item.company()).isEqualTo("Tech Corp");
        assertThat(item.location()).isEqualTo("New York, NY");
        assertThat(item.startDate()).isEqualTo(startDate);
        assertThat(item.endDate()).isEqualTo(endDate);
        assertThat(item.current()).isFalse();
        assertThat(item.summary()).isEqualTo("Led development team");
        assertThat(item.description()).isEqualTo("Developed microservices using Spring Boot");
    }

    @Test
    void shouldCreateCurrentExperienceItem() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 7, 1);

        // When
        ExperienceItem item = new ExperienceItem(
                2,
                "Tech Lead",
                "Startup Inc",
                "Remote",
                startDate,
                null,
                true,
                "Leading technical initiatives",
                "Architecture and development"
        );

        // Then
        assertThat(item.id()).isEqualTo(2);
        assertThat(item.role()).isEqualTo("Tech Lead");
        assertThat(item.current()).isTrue();
        assertThat(item.endDate()).isNull();
    }

    @Test
    void shouldCompareEqualExperienceItems() {
        // Given
        LocalDate startDate = LocalDate.of(2020, 1, 15);
        LocalDate endDate = LocalDate.of(2023, 6, 30);
        ExperienceItem item1 = new ExperienceItem(
                1,
                "Senior Java Developer",
                "Tech Corp",
                "New York, NY",
                startDate,
                endDate,
                false,
                "Led development team",
                "Developed microservices"
        );
        ExperienceItem item2 = new ExperienceItem(
                1,
                "Senior Java Developer",
                "Tech Corp",
                "New York, NY",
                startDate,
                endDate,
                false,
                "Led development team",
                "Developed microservices"
        );

        // Then
        assertThat(item1).isEqualTo(item2);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    @Test
    void shouldCompareDifferentExperienceItems() {
        // Given
        ExperienceItem item1 = new ExperienceItem(
                1,
                "Senior Java Developer",
                "Tech Corp",
                "New York, NY",
                LocalDate.of(2020, 1, 15),
                LocalDate.of(2023, 6, 30),
                false,
                "Led development team",
                "Developed microservices"
        );
        ExperienceItem item2 = new ExperienceItem(
                2,
                "Tech Lead",
                "Startup Inc",
                "Remote",
                LocalDate.of(2023, 7, 1),
                null,
                true,
                "Leading technical initiatives",
                "Architecture and development"
        );

        // Then
        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    void shouldHaveToStringMethod() {
        // Given
        ExperienceItem item = new ExperienceItem(
                1,
                "Senior Java Developer",
                "Tech Corp",
                "New York, NY",
                LocalDate.of(2020, 1, 15),
                LocalDate.of(2023, 6, 30),
                false,
                "Led development team",
                "Developed microservices"
        );

        // When
        String result = item.toString();

        // Then
        assertThat(result).contains("Senior Java Developer");
        assertThat(result).contains("Tech Corp");
    }
}
