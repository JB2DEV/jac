package com.jb2dev.cv.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvalidLanguageExceptionTest {

    @Test
    void shouldCreateExceptionWithInvalidCode() {
        // When
        InvalidLanguageException exception = new InvalidLanguageException("fr_FR");

        // Then
        assertThat(exception.getMessage()).contains("Invalid language code: 'fr_FR'");
        assertThat(exception.getMessage()).contains("es_ES");
        assertThat(exception.getMessage()).contains("en_EN");
        assertThat(exception.getInvalidCode()).isEqualTo("fr_FR");
        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    void shouldBeRuntimeException() {
        // When
        InvalidLanguageException exception = new InvalidLanguageException("invalid");

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldBeThrowable() {
        assertThatThrownBy(() -> {
            throw new InvalidLanguageException("de_DE");
        })
        .isInstanceOf(InvalidLanguageException.class)
        .hasMessageContaining("de_DE");
    }
}
