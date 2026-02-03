package com.jb2dev.cv.application.experience.impl;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.experience.model.ExperienceItem;
import com.jb2dev.cv.domain.experience.ports.ExperienceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetExperienceInteractorTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @InjectMocks
    private GetExperienceInteractor interactor;

    @Test
    void shouldReturnExperienceWhenFound() {
        // Given
        int id = 1;
        Language language = Language.EN_EN;
        ExperienceItem expectedExperience = new ExperienceItem(
                1,
                "Senior Java Developer",
                "Tech Corp",
                "New York, NY",
                LocalDate.of(2020, 1, 15),
                LocalDate.of(2023, 6, 30),
                false,
                "Led development team of 5 developers",
                "Developed microservices using Spring Boot and Kafka"
        );
        when(experienceRepository.findExperienceById(id, language)).thenReturn(Optional.of(expectedExperience));

        // When
        Optional<ExperienceItem> result = interactor.execute(id, language);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedExperience);
        assertThat(result.get().role()).isEqualTo("Senior Java Developer");
        assertThat(result.get().company()).isEqualTo("Tech Corp");
        verify(experienceRepository).findExperienceById(id, language);
    }

    @Test
    void shouldReturnEmptyWhenExperienceNotFound() {
        // Given
        int id = 999;
        Language language = Language.EN_EN;
        when(experienceRepository.findExperienceById(id, language)).thenReturn(Optional.empty());

        // When
        Optional<ExperienceItem> result = interactor.execute(id, language);

        // Then
        assertThat(result).isEmpty();
        verify(experienceRepository).findExperienceById(id, language);
    }

    @Test
    void shouldReturnExperienceInSpanish() {
        // Given
        int id = 1;
        Language language = Language.ES_ES;
        ExperienceItem expectedExperience = new ExperienceItem(
                1,
                "Desarrollador Java Senior",
                "Tech Corp",
                "Nueva York, NY",
                LocalDate.of(2020, 1, 15),
                LocalDate.of(2023, 6, 30),
                false,
                "Lideré un equipo de 5 desarrolladores",
                "Desarrollo de microservicios con Spring Boot y Kafka"
        );
        when(experienceRepository.findExperienceById(id, language)).thenReturn(Optional.of(expectedExperience));

        // When
        Optional<ExperienceItem> result = interactor.execute(id, language);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().role()).isEqualTo("Desarrollador Java Senior");
        verify(experienceRepository).findExperienceById(id, language);
    }

    @Test
    void shouldReturnCurrentExperience() {
        // Given
        int id = 2;
        Language language = Language.EN_EN;
        ExperienceItem currentExperience = new ExperienceItem(
                2,
                "Tech Lead",
                "Startup Inc",
                "Remote",
                LocalDate.of(2023, 7, 1),
                null,
                true,
                "Leading technical initiatives",
                "Architecture design and implementation"
        );
        when(experienceRepository.findExperienceById(id, language)).thenReturn(Optional.of(currentExperience));

        // When
        Optional<ExperienceItem> result = interactor.execute(id, language);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().current()).isTrue();
        assertThat(result.get().endDate()).isNull();
        verify(experienceRepository).findExperienceById(id, language);
    }
}
