package com.jb2dev.cv.domain.education.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EducationItemTest {

    @Test
    void shouldCreateEducationItem() {
        // Given
        LocalDate startDate = LocalDate.of(2015, 9, 1);
        LocalDate endDate = LocalDate.of(2019, 6, 30);

        // When
        EducationItem item = new EducationItem(
                1,
                "Bachelor of Computer Science",
                "State University",
                "Boston, MA",
                startDate,
                endDate,
                "Major in Software Engineering"
        );

        // Then
        assertThat(item.id()).isEqualTo(1);
        assertThat(item.title()).isEqualTo("Bachelor of Computer Science");
        assertThat(item.institution()).isEqualTo("State University");
        assertThat(item.location()).isEqualTo("Boston, MA");
        assertThat(item.startDate()).isEqualTo(startDate);
        assertThat(item.endDate()).isEqualTo(endDate);
        assertThat(item.details()).isEqualTo("Major in Software Engineering");
    }

    @Test
    void shouldCompareEqualEducationItems() {
        // Given
        LocalDate startDate = LocalDate.of(2015, 9, 1);
        LocalDate endDate = LocalDate.of(2019, 6, 30);
        EducationItem item1 = new EducationItem(
                1,
                "Bachelor of Computer Science",
                "State University",
                "Boston, MA",
                startDate,
                endDate,
                "Major in Software Engineering"
        );
        EducationItem item2 = new EducationItem(
                1,
                "Bachelor of Computer Science",
                "State University",
                "Boston, MA",
                startDate,
                endDate,
                "Major in Software Engineering"
        );

        // Then
        assertThat(item1).isEqualTo(item2);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    @Test
    void shouldCompareDifferentEducationItems() {
        // Given
        EducationItem item1 = new EducationItem(
                1,
                "Bachelor of Computer Science",
                "State University",
                "Boston, MA",
                LocalDate.of(2015, 9, 1),
                LocalDate.of(2019, 6, 30),
                "Major in Software Engineering"
        );
        EducationItem item2 = new EducationItem(
                2,
                "Master of Science",
                "Tech Institute",
                "San Francisco, CA",
                LocalDate.of(2019, 9, 1),
                LocalDate.of(2021, 6, 30),
                "Specialized in AI"
        );

        // Then
        assertThat(item1).isNotEqualTo(item2);
    }

    @Test
    void shouldHaveToStringMethod() {
        // Given
        EducationItem item = new EducationItem(
                1,
                "Bachelor of Computer Science",
                "State University",
                "Boston, MA",
                LocalDate.of(2015, 9, 1),
                LocalDate.of(2019, 6, 30),
                "Major in Software Engineering"
        );

        // When
        String result = item.toString();

        // Then
        assertThat(result).contains("Bachelor of Computer Science");
        assertThat(result).contains("State University");
    }
}
