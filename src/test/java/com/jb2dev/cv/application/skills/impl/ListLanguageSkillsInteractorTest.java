package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListLanguageSkillsInteractorTest {

    @Mock
    private SkillsRepository skillsRepository;

    @InjectMocks
    private ListLanguageSkillsInteractor interactor;

    @Test
    void shouldReturnAllLanguageSkillsInEnglish() {
        // Given
        Language language = Language.EN_EN;
        List<LanguageSkill> expectedSkills = List.of(
                new LanguageSkill(1, "English", "C2", "C2", "C2", "C2", "C2"),
                new LanguageSkill(2, "Spanish", "B2", "B2", "B1", "B1", "B2")
        );
        when(skillsRepository.findAllLanguages(language)).thenReturn(expectedSkills);

        // When
        List<LanguageSkill> result = interactor.execute(language);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedSkills);
        verify(skillsRepository).findAllLanguages(language);
    }

    @Test
    void shouldReturnAllLanguageSkillsInSpanish() {
        // Given
        Language language = Language.ES_ES;
        List<LanguageSkill> expectedSkills = List.of(
                new LanguageSkill(1, "Inglés", "C2", "C2", "C2", "C2", "C2")
        );
        when(skillsRepository.findAllLanguages(language)).thenReturn(expectedSkills);

        // When
        List<LanguageSkill> result = interactor.execute(language);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(expectedSkills);
        verify(skillsRepository).findAllLanguages(language);
    }

    @Test
    void shouldReturnEmptyListWhenNoLanguagesFound() {
        // Given
        Language language = Language.EN_EN;
        when(skillsRepository.findAllLanguages(language)).thenReturn(List.of());

        // When
        List<LanguageSkill> result = interactor.execute(language);

        // Then
        assertThat(result).isEmpty();
        verify(skillsRepository).findAllLanguages(language);
    }
}
