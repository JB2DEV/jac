package com.jb2dev.cv.application.exception;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UseCaseExecutionExceptionTest {

    @Test
    void shouldFormatMessageWithUseCaseName() {
        var exception = new UseCaseExecutionException("GetEducation", "not found");

        assertThat(exception.getUseCaseName()).isEqualTo("GetEducation");
        assertThat(exception.getMessage())
                .isEqualTo("Use case 'GetEducation' failed: not found");
    }

    @Test
    void shouldFormatMessageWithCause() {
        var cause = new RuntimeException("original");
        var exception = new UseCaseExecutionException("GetEducation", "unexpected", cause);

        assertThat(exception.getCause()).isSameAs(cause);
        assertThat(exception.getMessage())
                .isEqualTo("Use case 'GetEducation' failed: unexpected");
    }
}