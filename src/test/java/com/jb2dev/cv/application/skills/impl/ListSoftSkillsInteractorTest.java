package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
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
class ListSoftSkillsInteractorTest {

    @Mock
    private SkillsRepository skillsRepository;

    @InjectMocks
    private ListSoftSkillsInteractor interactor;

    @Test
    void shouldReturnAllSoftSkillsInEnglish() {
        // Given
        Language language = Language.EN_EN;
        List<SoftSkill> expectedSkills = List.of(
                new SoftSkill(1, "Communication"),
                new SoftSkill(2, "Teamwork"),
                new SoftSkill(3, "Leadership")
        );
        when(skillsRepository.findAllSoftSkills(language)).thenReturn(expectedSkills);

        // When
        List<SoftSkill> result = interactor.execute(language);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).isEqualTo(expectedSkills);
        verify(skillsRepository).findAllSoftSkills(language);
    }

    @Test
    void shouldReturnAllSoftSkillsInSpanish() {
        // Given
        Language language = Language.ES_ES;
        List<SoftSkill> expectedSkills = List.of(
                new SoftSkill(1, "Comunicación"),
                new SoftSkill(2, "Trabajo en equipo")
        );
        when(skillsRepository.findAllSoftSkills(language)).thenReturn(expectedSkills);

        // When
        List<SoftSkill> result = interactor.execute(language);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedSkills);
        verify(skillsRepository).findAllSoftSkills(language);
    }

    @Test
    void shouldReturnEmptyListWhenNoSkillsFound() {
        // Given
        Language language = Language.EN_EN;
        when(skillsRepository.findAllSoftSkills(language)).thenReturn(List.of());

        // When
        List<SoftSkill> result = interactor.execute(language);

        // Then
        assertThat(result).isEmpty();
        verify(skillsRepository).findAllSoftSkills(language);
    }
}
