package com.jb2dev.cv.application.education.impl;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.education.model.EducationItem;
import com.jb2dev.cv.domain.education.ports.EducationRepository;
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
class ListEducationsInteractorTest {

    @Mock
    private EducationRepository educationRepository;

    @InjectMocks
    private ListEducationsInteractor interactor;

    @Test
    void shouldReturnAllEducationsInEnglish() {
        // Given
        Language language = Language.EN_EN;
        List<EducationItem> expectedEducations = List.of(
                new EducationItem(
                        1,
                        "Bachelor of Computer Science",
                        "State University",
                        "Boston, MA",
                        LocalDate.of(2015, 9, 1),
                        LocalDate.of(2019, 6, 30),
                        "Major in Software Engineering, GPA 3.8"
                ),
                new EducationItem(
                        2,
                        "Master of Science in Computer Science",
                        "Tech Institute",
                        "San Francisco, CA",
                        LocalDate.of(2019, 9, 1),
                        LocalDate.of(2021, 6, 30),
                        "Specialized in Artificial Intelligence"
                )
        );
        when(educationRepository.findAllEducations(language)).thenReturn(expectedEducations);

        // When
        List<EducationItem> result = interactor.execute(language);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedEducations);
        verify(educationRepository).findAllEducations(language);
    }

    @Test
    void shouldReturnAllEducationsInSpanish() {
        // Given
        Language language = Language.ES_ES;
        List<EducationItem> expectedEducations = List.of(
                new EducationItem(
                        1,
                        "Grado en Ingeniería Informática",
                        "Universidad Estatal",
                        "Madrid, España",
                        LocalDate.of(2015, 9, 1),
                        LocalDate.of(2019, 6, 30),
                        "Especialidad en Ingeniería del Software, Nota media 8.5"
                )
        );
        when(educationRepository.findAllEducations(language)).thenReturn(expectedEducations);

        // When
        List<EducationItem> result = interactor.execute(language);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(expectedEducations);
        verify(educationRepository).findAllEducations(language);
    }

    @Test
    void shouldReturnEmptyListWhenNoEducationsFound() {
        // Given
        Language language = Language.EN_EN;
        when(educationRepository.findAllEducations(language)).thenReturn(List.of());

        // When
        List<EducationItem> result = interactor.execute(language);

        // Then
        assertThat(result).isEmpty();
        verify(educationRepository).findAllEducations(language);
    }
}
