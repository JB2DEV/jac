package com.jb2dev.cv.application.profile.impl;

import com.jb2dev.cv.domain.Language;
import com.jb2dev.cv.domain.profile.model.ContactInfo;
import com.jb2dev.cv.domain.profile.ports.ContactInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetContactInfoInteractorTest {

    @Mock
    private ContactInfoRepository contactInfoRepository;

    @InjectMocks
    private GetContactInfoInteractor interactor;

    @Test
    void shouldReturnContactInfoInEnglish() {
        // Given
        Language language = Language.EN_EN;
        ContactInfo expectedInfo = new ContactInfo(
                "123 Main St, Boston, MA",
                "john.doe@example.com",
                "+1-555-123-4567",
                "https://linkedin.com/in/johndoe",
                "https://github.com/johndoe"
        );
        when(contactInfoRepository.getContactInfo(language)).thenReturn(expectedInfo);

        // When
        ContactInfo result = interactor.execute(language);

        // Then
        assertThat(result).isEqualTo(expectedInfo);
        assertThat(result.email()).isEqualTo("john.doe@example.com");
        assertThat(result.githubUrl()).isEqualTo("https://github.com/johndoe");
        verify(contactInfoRepository).getContactInfo(language);
    }

    @Test
    void shouldReturnContactInfoInSpanish() {
        // Given
        Language language = Language.ES_ES;
        ContactInfo expectedInfo = new ContactInfo(
                "Calle Principal 123, Madrid, España",
                "juan.perez@ejemplo.com",
                "+34-666-123-456",
                "https://linkedin.com/in/juanperez",
                "https://github.com/juanperez"
        );
        when(contactInfoRepository.getContactInfo(language)).thenReturn(expectedInfo);

        // When
        ContactInfo result = interactor.execute(language);

        // Then
        assertThat(result).isEqualTo(expectedInfo);
        assertThat(result.email()).isEqualTo("juan.perez@ejemplo.com");
        assertThat(result.address()).contains("Madrid");
        verify(contactInfoRepository).getContactInfo(language);
    }
}
