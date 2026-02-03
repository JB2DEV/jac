package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.application.skills.query.TechnicalSkillSearchCriteria;
import com.jb2dev.cv.domain.skills.model.TechnicalSkill;
import com.jb2dev.cv.domain.skills.model.TechnicalSkillCategory;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyString;

@ExtendWith(MockitoExtension.class)
class SearchTechnicalSkillsInteractorTest {

    @Mock
    private SkillsRepository skillsRepository;

    @InjectMocks
    private SearchTechnicalSkillsInteractor interactor;

    private List<TechnicalSkill> expectedSkills;

    @BeforeEach
    void setUp() {
        expectedSkills = List.of(
                new TechnicalSkill(1, "Java", TechnicalSkillCategory.LANGUAGE, 5),
                new TechnicalSkill(2, "Spring", TechnicalSkillCategory.FRAMEWORK, 4)
        );
    }

    @Test
    void shouldSearchByNameWhenNameIsProvided() {
        // Given
        TechnicalSkillSearchCriteria criteria = new TechnicalSkillSearchCriteria("Java", null);
        List<TechnicalSkill> javaSkills = List.of(expectedSkills.getFirst());
        when(skillsRepository.findTechnicalSkillByName("Java")).thenReturn(javaSkills);

        // When
        List<TechnicalSkill> result = interactor.execute(criteria);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Java");
        verify(skillsRepository).findTechnicalSkillByName("Java");
        verify(skillsRepository, never()).findTechnicalSkillByCategory(anyString());
        verify(skillsRepository, never()).findAllTechnicalSkills();
    }

    @Test
    void shouldSearchByCategoryWhenCategoryIsProvidedAndNameIsNull() {
        // Given
        TechnicalSkillSearchCriteria criteria = new TechnicalSkillSearchCriteria(null, "Language");
        List<TechnicalSkill> languageSkills = List.of(expectedSkills.getFirst());
        when(skillsRepository.findTechnicalSkillByCategory("Language")).thenReturn(languageSkills);

        // When
        List<TechnicalSkill> result = interactor.execute(criteria);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().category()).isEqualTo(TechnicalSkillCategory.LANGUAGE);
        verify(skillsRepository).findTechnicalSkillByCategory("Language");
        verify(skillsRepository, never()).findTechnicalSkillByName(anyString());
        verify(skillsRepository, never()).findAllTechnicalSkills();
    }

    @Test
    void shouldReturnAllSkillsWhenNoSearchCriteria() {
        // Given
        TechnicalSkillSearchCriteria criteria = new TechnicalSkillSearchCriteria(null, null);
        when(skillsRepository.findAllTechnicalSkills()).thenReturn(expectedSkills);

        // When
        List<TechnicalSkill> result = interactor.execute(criteria);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedSkills);
        verify(skillsRepository).findAllTechnicalSkills();
        verify(skillsRepository, never()).findTechnicalSkillByName(anyString());
        verify(skillsRepository, never()).findTechnicalSkillByCategory(anyString());
    }

    @Test
    void shouldPrioritizeNameOverCategory() {
        // Given
        TechnicalSkillSearchCriteria criteria = new TechnicalSkillSearchCriteria("Java", "Language");
        List<TechnicalSkill> javaSkills = List.of(expectedSkills.getFirst());
        when(skillsRepository.findTechnicalSkillByName("Java")).thenReturn(javaSkills);

        // When
        List<TechnicalSkill> result = interactor.execute(criteria);

        // Then
        assertThat(result).hasSize(1);
        verify(skillsRepository).findTechnicalSkillByName("Java");
        verify(skillsRepository, never()).findTechnicalSkillByCategory(anyString());
        verify(skillsRepository, never()).findAllTechnicalSkills();
    }

    @Test
    void shouldReturnEmptyListWhenNoSkillsFound() {
        // Given
        TechnicalSkillSearchCriteria criteria = new TechnicalSkillSearchCriteria("NonExistent", null);
        when(skillsRepository.findTechnicalSkillByName("NonExistent")).thenReturn(List.of());

        // When
        List<TechnicalSkill> result = interactor.execute(criteria);

        // Then
        assertThat(result).isEmpty();
        verify(skillsRepository).findTechnicalSkillByName("NonExistent");
    }
}
