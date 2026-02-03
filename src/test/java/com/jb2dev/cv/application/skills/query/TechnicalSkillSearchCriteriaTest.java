package com.jb2dev.cv.application.skills.query;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TechnicalSkillSearchCriteriaTest {

    @Test
    void shouldCreateCriteriaWithNameOnly() {
        // When
        TechnicalSkillSearchCriteria criteria = new TechnicalSkillSearchCriteria("Java", null);

        // Then
        assertThat(criteria.name()).isEqualTo("Java");
        assertThat(criteria.category()).isNull();
    }

    @Test
    void shouldCreateCriteriaWithCategoryOnly() {
        // When
        TechnicalSkillSearchCriteria criteria = new TechnicalSkillSearchCriteria(null, "Language");

        // Then
        assertThat(criteria.name()).isNull();
        assertThat(criteria.category()).isEqualTo("Language");
    }

    @Test
    void shouldCreateCriteriaWithBothParameters() {
        // When
        TechnicalSkillSearchCriteria criteria = new TechnicalSkillSearchCriteria("Java", "Language");

        // Then
        assertThat(criteria.name()).isEqualTo("Java");
        assertThat(criteria.category()).isEqualTo("Language");
    }

    @Test
    void shouldCreateCriteriaWithNullParameters() {
        // When
        TechnicalSkillSearchCriteria criteria = new TechnicalSkillSearchCriteria(null, null);

        // Then
        assertThat(criteria.name()).isNull();
        assertThat(criteria.category()).isNull();
    }

    @Test
    void shouldCompareEqualCriteria() {
        // Given
        TechnicalSkillSearchCriteria criteria1 = new TechnicalSkillSearchCriteria("Java", "Language");
        TechnicalSkillSearchCriteria criteria2 = new TechnicalSkillSearchCriteria("Java", "Language");

        // Then
        assertThat(criteria1).isEqualTo(criteria2);
        assertThat(criteria1.hashCode()).isEqualTo(criteria2.hashCode());
    }

    @Test
    void shouldCompareDifferentCriteria() {
        // Given
        TechnicalSkillSearchCriteria criteria1 = new TechnicalSkillSearchCriteria("Java", "Language");
        TechnicalSkillSearchCriteria criteria2 = new TechnicalSkillSearchCriteria("Spring", "Framework");

        // Then
        assertThat(criteria1).isNotEqualTo(criteria2);
    }
}
