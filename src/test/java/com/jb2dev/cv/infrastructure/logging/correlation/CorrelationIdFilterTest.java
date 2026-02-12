package com.jb2dev.cv.infrastructure.logging.correlation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrelationIdFilterTest {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Mock
    private CorrelationIdGenerator generator;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private CorrelationIdFilter filter;

    @BeforeEach
    void setUp() {
        filter = new CorrelationIdFilter(generator);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/test");
    }

    @AfterEach
    void cleanup() {
        CorrelationIdHolder.clear();
    }

    @Test
    void shouldGenerateNewCorrelationIdWhenHeaderNotPresent() throws Exception {
        // Given
        String generatedId = "req-generated-123";
        when(request.getHeader(CORRELATION_ID_HEADER)).thenReturn(null);
        when(generator.generate()).thenReturn(generatedId);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(generator).generate();
        verify(response).setHeader(CORRELATION_ID_HEADER, generatedId);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldUseExistingCorrelationIdFromHeader() throws Exception {
        // Given
        String existingId = "req-from-client-456";
        when(request.getHeader(CORRELATION_ID_HEADER)).thenReturn(existingId);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(generator, never()).generate();
        verify(response).setHeader(CORRELATION_ID_HEADER, existingId);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldClearMdcAfterRequest() throws Exception {
        // Given
        when(request.getHeader(CORRELATION_ID_HEADER)).thenReturn("test-id");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then - MDC should be cleared after filter execution
        assertThat(CorrelationIdHolder.get()).isNull();
    }

    @Test
    void shouldClearMdcEvenWhenExceptionOccurs() throws Exception {
        // Given
        when(request.getHeader(CORRELATION_ID_HEADER)).thenReturn("test-id");
        doThrow(new RuntimeException("Test exception")).when(filterChain).doFilter(request, response);

        // When
        try {
            filter.doFilterInternal(request, response, filterChain);
        } catch (RuntimeException e) {
            // Expected
        }

        // Then - MDC should still be cleared
        assertThat(CorrelationIdHolder.get()).isNull();
    }

    @Test
    void shouldGenerateNewIdWhenHeaderIsBlank() throws Exception {
        // Given
        String generatedId = "req-new-789";
        when(request.getHeader(CORRELATION_ID_HEADER)).thenReturn("   ");
        when(generator.generate()).thenReturn(generatedId);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(generator).generate();
        verify(response).setHeader(CORRELATION_ID_HEADER, generatedId);
    }
}
