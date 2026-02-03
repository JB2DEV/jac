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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTrainingInteractorTest {

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private GetTrainingInteractor interactor;

    @Test
    void shouldReturnTrainingWhenFound() {
        // Given
        String credentialId = "CERT-12345";
        Language language = Language.EN_EN;
        TrainingItem expectedTraining = new TrainingItem(
                1,
                "AWS Certified Solutions Architect",
                "Amazon Web Services",
                "Online",
                YearMonth.of(2023, 6),
                credentialId,
                "https://aws.amazon.com/certification/verify/12345",
                "Professional level certification"
        );
        when(trainingRepository.findTrainingById(credentialId, language)).thenReturn(Optional.of(expectedTraining));

        // When
        Optional<TrainingItem> result = interactor.execute(credentialId, language);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedTraining);
        assertThat(result.get().credentialId()).isEqualTo(credentialId);
        assertThat(result.get().title()).isEqualTo("AWS Certified Solutions Architect");
        verify(trainingRepository).findTrainingById(credentialId, language);
    }

    @Test
    void shouldReturnEmptyWhenTrainingNotFound() {
        // Given
        String credentialId = "CERT-NOTFOUND";
        Language language = Language.EN_EN;
        when(trainingRepository.findTrainingById(credentialId, language)).thenReturn(Optional.empty());

        // When
        Optional<TrainingItem> result = interactor.execute(credentialId, language);

        // Then
        assertThat(result).isEmpty();
        verify(trainingRepository).findTrainingById(credentialId, language);
    }

    @Test
    void shouldReturnTrainingInSpanish() {
        // Given
        String credentialId = "CERT-12345";
        Language language = Language.ES_ES;
        TrainingItem expectedTraining = new TrainingItem(
                1,
                "Certificación AWS Solutions Architect",
                "Amazon Web Services",
                "En línea",
                YearMonth.of(2023, 6),
                credentialId,
                "https://aws.amazon.com/certification/verify/12345",
                "Certificación nivel profesional"
        );
        when(trainingRepository.findTrainingById(credentialId, language)).thenReturn(Optional.of(expectedTraining));

        // When
        Optional<TrainingItem> result = interactor.execute(credentialId, language);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().title()).isEqualTo("Certificación AWS Solutions Architect");
        verify(trainingRepository).findTrainingById(credentialId, language);
    }
}
