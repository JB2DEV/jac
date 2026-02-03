package com.jb2dev.cv.application.training.impl;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.training.model.TrainingItem;
import com.jb2dev.cv.domain.training.ports.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListTrainingsInteractorTest {

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private ListTrainingsInteractor interactor;

    @Test
    void shouldReturnAllTrainingsInEnglish() {
        // Given
        Language language = Language.EN_EN;
        List<TrainingItem> expectedTrainings = List.of(
                new TrainingItem(
                        1,
                        "AWS Certified Solutions Architect",
                        "Amazon Web Services",
                        "Online",
                        YearMonth.of(2023, 6),
                        "CERT-12345",
                        "https://aws.amazon.com/certification/verify/12345",
                        "Professional level certification"
                ),
                new TrainingItem(
                        2,
                        "Spring Framework Certification",
                        "VMware",
                        "Online",
                        YearMonth.of(2023, 3),
                        "CERT-67890",
                        "https://spring.io/certification/verify/67890",
                        "Core Spring certification"
                )
        );
        when(trainingRepository.findAllTrainings(language)).thenReturn(expectedTrainings);

        // When
        List<TrainingItem> result = interactor.execute(language);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedTrainings);
        verify(trainingRepository).findAllTrainings(language);
    }

    @Test
    void shouldReturnAllTrainingsInSpanish() {
        // Given
        Language language = Language.ES_ES;
        List<TrainingItem> expectedTrainings = List.of(
                new TrainingItem(
                        1,
                        "Certificación AWS Solutions Architect",
                        "Amazon Web Services",
                        "En línea",
                        YearMonth.of(2023, 6),
                        "CERT-12345",
                        "https://aws.amazon.com/certification/verify/12345",
                        "Certificación nivel profesional"
                )
        );
        when(trainingRepository.findAllTrainings(language)).thenReturn(expectedTrainings);

        // When
        List<TrainingItem> result = interactor.execute(language);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(expectedTrainings);
        verify(trainingRepository).findAllTrainings(language);
    }

    @Test
    void shouldReturnEmptyListWhenNoTrainingsFound() {
        // Given
        Language language = Language.EN_EN;
        when(trainingRepository.findAllTrainings(language)).thenReturn(List.of());

        // When
        List<TrainingItem> result = interactor.execute(language);

        // Then
        assertThat(result).isEmpty();
        verify(trainingRepository).findAllTrainings(language);
    }
}
