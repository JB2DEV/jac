package com.jb2dev.cv.application.skills.impl;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.skills.model.SoftSkill;
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
class GetSoftSkillInteractorTest {

    @Mock
    private SkillsRepository skillsRepository;

    @InjectMocks
    private GetSoftSkillInteractor interactor;

    @Test
    void shouldReturnSoftSkillWhenFound() {
        // Given
        int id = 1;
        Language language = Language.EN_EN;
        SoftSkill expectedSkill = new SoftSkill(1, "Communication");
        when(skillsRepository.findSoftSkillById(id, language)).thenReturn(Optional.of(expectedSkill));

        // When
        Optional<SoftSkill> result = interactor.execute(id, language);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedSkill);
        assertThat(result.get().name()).isEqualTo("Communication");
        verify(skillsRepository).findSoftSkillById(id, language);
    }

    @Test
    void shouldReturnEmptyWhenSkillNotFound() {
        // Given
        int id = 999;
        Language language = Language.EN_EN;
        when(skillsRepository.findSoftSkillById(id, language)).thenReturn(Optional.empty());

        // When
        Optional<SoftSkill> result = interactor.execute(id, language);

        // Then
        assertThat(result).isEmpty();
        verify(skillsRepository).findSoftSkillById(id, language);
    }

    @Test
    void shouldReturnSoftSkillInSpanish() {
        // Given
        int id = 1;
        Language language = Language.ES_ES;
        SoftSkill expectedSkill = new SoftSkill(1, "Comunicación");
        when(skillsRepository.findSoftSkillById(id, language)).thenReturn(Optional.of(expectedSkill));

        // When
        Optional<SoftSkill> result = interactor.execute(id, language);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Comunicación");
        verify(skillsRepository).findSoftSkillById(id, language);
    }
}
