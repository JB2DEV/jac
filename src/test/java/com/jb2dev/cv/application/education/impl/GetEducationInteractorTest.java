package com.jb2dev.cv.application.education.impl;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.education.model.EducationItem;
import com.jb2dev.cv.domain.education.ports.EducationRepository;
import com.jb2dev.cv.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetEducationInteractorTest {

    @Mock
    private EducationRepository educationRepository;

    @InjectMocks
    private GetEducationInteractor interactor;

    @Test
    void shouldReturnEducationWhenFound() {
        // Given
        int id = 1;
        Language language = Language.EN_EN;
        EducationItem expectedEducation = new EducationItem(
                1,
                "Bachelor of Computer Science",
                "State University",
                "Boston, MA",
                LocalDate.of(2015, 9, 1),
                LocalDate.of(2019, 6, 30),
                "Major in Software Engineering, GPA 3.8"
        );
        when(educationRepository.findEducationById(id, language)).thenReturn(Optional.of(expectedEducation));

        // When
        EducationItem result = interactor.execute(id, language);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedEducation);
        assertThat(result.title()).isEqualTo("Bachelor of Computer Science");
        assertThat(result.institution()).isEqualTo("State University");
        verify(educationRepository).findEducationById(id, language);
    }

    @Test
    void shouldThrowExceptionWhenEducationNotFound() {
        // Given
        int id = 999;
        Language language = Language.EN_EN;
        when(educationRepository.findEducationById(id, language)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> interactor.execute(id, language))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Education")
                .hasMessageContaining("999");

        verify(educationRepository).findEducationById(id, language);
    }

    @Test
    void shouldReturnEducationInSpanish() {
        // Given
        int id = 1;
        Language language = Language.ES_ES;
        EducationItem expectedEducation = new EducationItem(
                1,
                "Grado en Ingeniería Informática",
                "Universidad Estatal",
                "Madrid, España",
                LocalDate.of(2015, 9, 1),
                LocalDate.of(2019, 6, 30),
                "Especialidad en Ingeniería del Software, Nota media 8.5"
        );
        when(educationRepository.findEducationById(id, language)).thenReturn(Optional.of(expectedEducation));

        // When
        EducationItem result = interactor.execute(id, language);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Grado en Ingeniería Informática");
        verify(educationRepository).findEducationById(id, language);
    }
}
