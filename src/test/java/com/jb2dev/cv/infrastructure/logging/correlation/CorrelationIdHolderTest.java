package com.jb2dev.cv.infrastructure.logging.correlation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdHolderTest {

    @BeforeEach
    @AfterEach
    void cleanup() {
        // Ensure MDC is clean before and after each test
        CorrelationIdHolder.clear();
    }

    @Test
    void shouldSetAndGetCorrelationId() {
        // Given
        String correlationId = "test-correlation-id-123";

        // When
        CorrelationIdHolder.set(correlationId);

        // Then
        assertThat(CorrelationIdHolder.get()).isEqualTo(correlationId);
    }

    @Test
    void shouldReturnNullWhenNotSet() {
        // When
        String result = CorrelationIdHolder.get();

        // Then
        assertThat(result).isNull();
    }

    @Test
    void shouldClearCorrelationId() {
        // Given
        CorrelationIdHolder.set("test-id");

        // When
        CorrelationIdHolder.clear();

        // Then
        assertThat(CorrelationIdHolder.get()).isNull();
    }

    @Test
    void shouldOverwritePreviousCorrelationId() {
        // Given
        CorrelationIdHolder.set("first-id");

        // When
        CorrelationIdHolder.set("second-id");

        // Then
        assertThat(CorrelationIdHolder.get()).isEqualTo("second-id");
    }
}
