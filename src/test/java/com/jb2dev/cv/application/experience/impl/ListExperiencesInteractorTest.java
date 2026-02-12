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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListExperiencesInteractorTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @InjectMocks
    private ListExperiencesInteractor interactor;

    @Test
    void shouldReturnAllExperiencesInEnglish() {
        // Given
        Language language = Language.EN_EN;
        List<ExperienceItem> expectedExperiences = List.of(
                new ExperienceItem(
                        1,
                        "Senior Java Developer",
                        "Tech Corp",
                        "New York, NY",
                        LocalDate.of(2020, 1, 15),
                        LocalDate.of(2023, 6, 30),
                        false,
                        "Led development team of 5 developers",
                        "Developed microservices using Spring Boot and Kafka"
                ),
                new ExperienceItem(
                        2,
                        "Tech Lead",
                        "Startup Inc",
                        "Remote",
                        LocalDate.of(2023, 7, 1),
                        null,
                        true,
                        "Leading technical initiatives",
                        "Architecture design and implementation"
                )
        );
        when(experienceRepository.findAllExperiences(language)).thenReturn(expectedExperiences);

        // When
        List<ExperienceItem> result = interactor.execute(language);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedExperiences);
        verify(experienceRepository).findAllExperiences(language);
    }

    @Test
    void shouldReturnAllExperiencesInSpanish() {
        // Given
        Language language = Language.ES_ES;
        List<ExperienceItem> expectedExperiences = List.of(
                new ExperienceItem(
                        1,
                        "Desarrollador Java Senior",
                        "Tech Corp",
                        "Nueva York, NY",
                        LocalDate.of(2020, 1, 15),
                        LocalDate.of(2023, 6, 30),
                        false,
                        "Lideré un equipo de 5 desarrolladores",
                        "Desarrollo de microservicios con Spring Boot y Kafka"
                )
        );
        when(experienceRepository.findAllExperiences(language)).thenReturn(expectedExperiences);

        // When
        List<ExperienceItem> result = interactor.execute(language);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(expectedExperiences);
        verify(experienceRepository).findAllExperiences(language);
    }

    @Test
    void shouldReturnEmptyListWhenNoExperiencesFound() {
        // Given
        Language language = Language.EN_EN;
        when(experienceRepository.findAllExperiences(language)).thenReturn(List.of());

        // When
        List<ExperienceItem> result = interactor.execute(language);

        // Then
        assertThat(result).isEmpty();
        verify(experienceRepository).findAllExperiences(language);
    }
}
