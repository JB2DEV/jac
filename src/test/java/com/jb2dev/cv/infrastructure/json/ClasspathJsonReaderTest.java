package com.jb2dev.cv.infrastructure.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClasspathJsonReaderIntegrationTest {

    private ClasspathJsonReader reader;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        reader = new ClasspathJsonReader(objectMapper);
    }

    @Test
    void shouldReadJsonFileWithClass() {
        List<?> result = reader.read("data/commons/skills_technical.json", List.class);
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldReadJsonFileWithTypeReference() {
        List<?> result = reader.read(
                "data/en/skills_soft.json",
                new TypeReference<List<?>>() {}
        );
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldThrowExceptionWhenFileNotFound() {
        assertThatThrownBy(() ->
                reader.read("non-existent-file.json", List.class)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to read JSON resource");
    }

    @Test
    void shouldThrowExceptionWhenFileNotFoundWithTypeReference() {
        assertThatThrownBy(() ->
                reader.read("non-existent-file.json", new TypeReference<List<String>>() {})
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to read JSON resource");
    }

    @Test
    void shouldThrowExceptionWhenInvalidJsonFormat() {
        assertThatThrownBy(() ->
                reader.read("application.yml", List.class)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to read JSON resource");
    }
}
