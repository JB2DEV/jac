package com.jb2dev.cv.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidAggregateStateExceptionTest {

    @Test
    void shouldCreateExceptionWithAggregateTypeAndReason() {
        // When
        InvalidAggregateStateException exception = new InvalidAggregateStateException(
            "EducationItem", "End date cannot be before start date");

        // Then
        assertThat(exception.getMessage()).isEqualTo(
            "Invalid state for EducationItem: End date cannot be before start date");
        assertThat(exception.getAggregateType()).isEqualTo("EducationItem");
        assertThat(exception.getReason()).isEqualTo("End date cannot be before start date");
        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    void shouldBeRuntimeException() {
        // When
        InvalidAggregateStateException exception = new InvalidAggregateStateException(
            "Test", "Invalid state");

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
