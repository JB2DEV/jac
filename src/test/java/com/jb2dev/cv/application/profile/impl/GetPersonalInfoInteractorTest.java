package com.jb2dev.cv.application.profile.impl;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.profile.model.PersonalInfo;
import com.jb2dev.cv.domain.profile.ports.PersonalInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPersonalInfoInteractorTest {

    @Mock
    private PersonalInfoRepository personalInfoRepository;

    @InjectMocks
    private GetPersonalInfoInteractor interactor;

    @Test
    void shouldReturnPersonalInfoInEnglish() {
        // Given
        Language language = Language.EN_EN;
        PersonalInfo expectedInfo = new PersonalInfo(
                "John Doe",
                "January 15, 1990",
                "American",
                "Male"
        );
        when(personalInfoRepository.getPersonalInfo(language)).thenReturn(expectedInfo);

        // When
        PersonalInfo result = interactor.execute(language);

        // Then
        assertThat(result).isEqualTo(expectedInfo);
        assertThat(result.fullName()).isEqualTo("John Doe");
        assertThat(result.nationality()).isEqualTo("American");
        verify(personalInfoRepository).getPersonalInfo(language);
    }

    @Test
    void shouldReturnPersonalInfoInSpanish() {
        // Given
        Language language = Language.ES_ES;
        PersonalInfo expectedInfo = new PersonalInfo(
                "Juan Pérez",
                "15 de enero de 1990",
                "Español",
                "Masculino"
        );
        when(personalInfoRepository.getPersonalInfo(language)).thenReturn(expectedInfo);

        // When
        PersonalInfo result = interactor.execute(language);

        // Then
        assertThat(result).isEqualTo(expectedInfo);
        assertThat(result.fullName()).isEqualTo("Juan Pérez");
        assertThat(result.nationality()).isEqualTo("Español");
        verify(personalInfoRepository).getPersonalInfo(language);
    }
}
