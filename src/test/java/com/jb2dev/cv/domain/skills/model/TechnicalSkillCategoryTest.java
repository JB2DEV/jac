package com.jb2dev.cv.domain.skills.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TechnicalSkillCategoryTest {

    @Test
    void shouldReturnCorrectLabel() {
        // When & Then
        assertThat(TechnicalSkillCategory.LANGUAGE.getLabel()).isEqualTo("Language");
        assertThat(TechnicalSkillCategory.FRAMEWORK.getLabel()).isEqualTo("Framework");
        assertThat(TechnicalSkillCategory.DEVOPS.getLabel()).isEqualTo("DevOps");
        assertThat(TechnicalSkillCategory.CICD.getLabel()).isEqualTo("CI/CD");
        assertThat(TechnicalSkillCategory.API.getLabel()).isEqualTo("API");
        assertThat(TechnicalSkillCategory.VCS.getLabel()).isEqualTo("VCS");
        assertThat(TechnicalSkillCategory.METHODOLOGY.getLabel()).isEqualTo("Methodology");
        assertThat(TechnicalSkillCategory.DATABASE.getLabel()).isEqualTo("Database");
        assertThat(TechnicalSkillCategory.TESTING.getLabel()).isEqualTo("Testing");
        assertThat(TechnicalSkillCategory.CODE_QUALITY.getLabel()).isEqualTo("Code Quality");
        assertThat(TechnicalSkillCategory.ARCHITECTURE.getLabel()).isEqualTo("Architecture");
        assertThat(TechnicalSkillCategory.DEVELOPMENT.getLabel()).isEqualTo("Development");
        assertThat(TechnicalSkillCategory.STREAMING.getLabel()).isEqualTo("Streaming");
        assertThat(TechnicalSkillCategory.AUTOMATION.getLabel()).isEqualTo("Automation");
        assertThat(TechnicalSkillCategory.PLATFORM.getLabel()).isEqualTo("Platform");
        assertThat(TechnicalSkillCategory.DATA.getLabel()).isEqualTo("Data");
    }

    @Test
    void shouldReturnCategoryFromJsonByLabel() {
        // When
        TechnicalSkillCategory category = TechnicalSkillCategory.fromJson("Language");

        // Then
        assertThat(category).isEqualTo(TechnicalSkillCategory.LANGUAGE);
    }

    @Test
    void shouldReturnCategoryFromJsonByLabelCaseInsensitive() {
        // When
        TechnicalSkillCategory category1 = TechnicalSkillCategory.fromJson("language");
        TechnicalSkillCategory category2 = TechnicalSkillCategory.fromJson("FRAMEWORK");

        // Then
        assertThat(category1).isEqualTo(TechnicalSkillCategory.LANGUAGE);
        assertThat(category2).isEqualTo(TechnicalSkillCategory.FRAMEWORK);
    }

    @Test
    void shouldReturnCategoryFromJsonByEnumName() {
        // When
        TechnicalSkillCategory category = TechnicalSkillCategory.fromJson("LANGUAGE");

        // Then
        assertThat(category).isEqualTo(TechnicalSkillCategory.LANGUAGE);
    }

    @Test
    void shouldReturnCategoryFromJsonByEnumNameCaseInsensitive() {
        // When
        TechnicalSkillCategory category = TechnicalSkillCategory.fromJson("devops");

        // Then
        assertThat(category).isEqualTo(TechnicalSkillCategory.DEVOPS);
    }

    @Test
    void shouldThrowExceptionWhenInvalidJsonValue() {
        // When & Then
        assertThatThrownBy(() -> TechnicalSkillCategory.fromJson("InvalidCategory"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown TechnicalSkillCategory: InvalidCategory");
    }

    @Test
    void shouldReturnCategoryFromId() {
        // When
        TechnicalSkillCategory category1 = TechnicalSkillCategory.fromId(1);
        TechnicalSkillCategory category2 = TechnicalSkillCategory.fromId(2);
        TechnicalSkillCategory category16 = TechnicalSkillCategory.fromId(16);

        // Then
        assertThat(category1).isEqualTo(TechnicalSkillCategory.LANGUAGE);
        assertThat(category2).isEqualTo(TechnicalSkillCategory.FRAMEWORK);
        assertThat(category16).isEqualTo(TechnicalSkillCategory.DATA);
    }

    @Test
    void shouldThrowExceptionWhenInvalidId() {
        // When & Then
        assertThatThrownBy(() -> TechnicalSkillCategory.fromId(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown TechnicalSkillCategory id: 999");
    }

    @Test
    void shouldReturnAllValues() {
        // When
        TechnicalSkillCategory[] categories = TechnicalSkillCategory.values();

        // Then
        assertThat(categories).hasSize(16);
    }

    @Test
    void shouldTestAllCategoryIds() {
        // When & Then
        for (int i = 1; i <= 16; i++) {
            TechnicalSkillCategory category = TechnicalSkillCategory.fromId(i);
            assertThat(category).isNotNull();
        }
    }
}
