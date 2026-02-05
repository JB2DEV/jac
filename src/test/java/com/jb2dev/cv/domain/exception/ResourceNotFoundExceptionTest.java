package com.jb2dev.cv.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithResourceTypeAndId() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException("Education", "1234");

        // Then
        assertThat(exception.getMessage()).isEqualTo("Education with id '1234' not found");
        assertThat(exception.getResourceType()).isEqualTo("Education");
        assertThat(exception.getResourceId()).isEqualTo("1234");
        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    void shouldCreateExceptionWithCause() {
        // Given
        Throwable cause = new RuntimeException("Original error");

        // When
        ResourceNotFoundException exception = new ResourceNotFoundException("Skill", "5678", cause);

        // Then
        assertThat(exception.getMessage()).isEqualTo("Skill with id '5678' not found");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getResourceType()).isEqualTo("Skill");
        assertThat(exception.getResourceId()).isEqualTo("5678");
    }

    @Test
    void shouldBeRuntimeException() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException("Test", "1");

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldBeThrowable() {
        assertThatThrownBy(() -> {
            throw new ResourceNotFoundException("Experience", "9999");
        })
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Experience")
        .hasMessageContaining("9999");
    }
}
