package com.jb2dev.cv.domain;

import com.jb2dev.cv.domain.exception.InvalidLanguageException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LanguageTest {

    @Test
    void shouldReturnCorrectCodeForEsEs() {
        // When
        String code = Language.ES_ES.getCode();

        // Then
        assertThat(code).isEqualTo("es_ES");
    }

    @Test
    void shouldReturnCorrectCodeForEnEn() {
        // When
        String code = Language.EN_EN.getCode();

        // Then
        assertThat(code).isEqualTo("en_EN");
    }

    @Test
    void shouldReturnLanguageFromCodeCaseSensitive() {
        // When
        Language language = Language.fromCode("es_ES");

        // Then
        assertThat(language).isEqualTo(Language.ES_ES);
    }

    @Test
    void shouldReturnLanguageFromCodeCaseInsensitive() {
        // When
        Language language1 = Language.fromCode("ES_ES");
        Language language2 = Language.fromCode("en_en");

        // Then
        assertThat(language1).isEqualTo(Language.ES_ES);
        assertThat(language2).isEqualTo(Language.EN_EN);
    }

    @Test
    void shouldThrowExceptionWhenInvalidCode() {
        // When & Then
        assertThatThrownBy(() -> Language.fromCode("fr_FR"))
                .isInstanceOf(InvalidLanguageException.class)
                .hasMessageContaining("fr_FR");
    }

    @Test
    void shouldThrowExceptionWhenNullCode() {
        // When & Then
        assertThatThrownBy(() -> Language.fromCode(null))
                .isInstanceOf(InvalidLanguageException.class)
                .hasMessageContaining("null");
    }

    @Test
    void shouldReturnAllValues() {
        // When
        Language[] languages = Language.values();

        // Then
        assertThat(languages).hasSize(2);
        assertThat(languages).contains(Language.ES_ES, Language.EN_EN);
    }

    @Test
    void shouldReturnCorrectValueOf() {
        // When
        Language language = Language.valueOf("ES_ES");

        // Then
        assertThat(language).isEqualTo(Language.ES_ES);
    }
}
