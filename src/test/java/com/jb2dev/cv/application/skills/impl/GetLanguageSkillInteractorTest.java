package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.LanguageSkill;
import com.jb2dev.cv.domain.skills.ports.SkillsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLanguageSkillInteractorTest {

    @Mock
    private SkillsRepository skillsRepository;

    @InjectMocks
    private GetLanguageSkillInteractor interactor;

    @Test
    void shouldReturnLanguageSkillWhenFound() {
        // Given
        int id = 1;
        Language language = Language.EN_EN;
        LanguageSkill expectedSkill = new LanguageSkill(1, "English", "C2", "C2", "C2", "C2", "C2");
        when(skillsRepository.findLanguageById(id, language)).thenReturn(Optional.of(expectedSkill));

        // When
        Optional<LanguageSkill> result = interactor.execute(id, language);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedSkill);
        assertThat(result.get().language()).isEqualTo("English");
        verify(skillsRepository).findLanguageById(id, language);
    }

    @Test
    void shouldReturnEmptyWhenLanguageSkillNotFound() {
        // Given
        int id = 999;
        Language language = Language.EN_EN;
        when(skillsRepository.findLanguageById(id, language)).thenReturn(Optional.empty());

        // When
        Optional<LanguageSkill> result = interactor.execute(id, language);

        // Then
        assertThat(result).isEmpty();
        verify(skillsRepository).findLanguageById(id, language);
    }

    @Test
    void shouldReturnLanguageSkillInSpanish() {
        // Given
        int id = 1;
        Language language = Language.ES_ES;
        LanguageSkill expectedSkill = new LanguageSkill(1, "Inglés", "C2", "C2", "C2", "C2", "C2");
        when(skillsRepository.findLanguageById(id, language)).thenReturn(Optional.of(expectedSkill));

        // When
        Optional<LanguageSkill> result = interactor.execute(id, language);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().language()).isEqualTo("Inglés");
        verify(skillsRepository).findLanguageById(id, language);
    }
}
