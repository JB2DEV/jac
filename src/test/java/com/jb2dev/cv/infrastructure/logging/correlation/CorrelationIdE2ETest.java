package com.jb2dev.cv.infrastructure.logging.correlation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * E2E Integration test to verify correlation ID functionality across the entire stack.
 * This test validates that:
 * - Custom correlation IDs from clients are preserved
 * - Correlation IDs are auto-generated when not provided
 * - Correlation IDs are returned in response headers
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CorrelationIdE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldPreserveCustomCorrelationIdFromClient() {
        // Given
        String customCorrelationId = "client-provided-correlation-id-123";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Correlation-Id", customCorrelationId);
        headers.set("Accept-Language", "es_ES");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/personal",
                HttpMethod.GET,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst("X-Correlation-Id"))
                .isEqualTo(customCorrelationId);
    }

    @Test
    void shouldGenerateCorrelationIdWhenNotProvided() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", "es_ES");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/personal",
                HttpMethod.GET,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String correlationId = response.getHeaders().getFirst("X-Correlation-Id");
        assertThat(correlationId)
                .isNotNull()
                .startsWith("req-")
                .matches("req-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    void shouldGenerateUniqueCorrelationIdsForDifferentRequests() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", "es_ES");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response1 = restTemplate.exchange(
                "/api/v1/personal",
                HttpMethod.GET,
                request,
                String.class
        );

        ResponseEntity<String> response2 = restTemplate.exchange(
                "/api/v1/contact",
                HttpMethod.GET,
                request,
                String.class
        );

        // Then
        String correlationId1 = response1.getHeaders().getFirst("X-Correlation-Id");
        String correlationId2 = response2.getHeaders().getFirst("X-Correlation-Id");

        assertThat(correlationId1).isNotNull();
        assertThat(correlationId2).isNotNull();
        assertThat(correlationId1).isNotEqualTo(correlationId2);
    }

    @Test
    void shouldIncludeCorrelationIdInErrorResponses() {
        // Given
        String customCorrelationId = "error-test-correlation-id";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Correlation-Id", customCorrelationId);
        headers.set("Accept-Language", "invalid_language");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/personal",
                HttpMethod.GET,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getHeaders().getFirst("X-Correlation-Id"))
                .isEqualTo(customCorrelationId);
    }

    @Test
    void shouldWorkWithAllEndpoints() {
        // Given
        String correlationId = "test-all-endpoints-123";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Correlation-Id", correlationId);
        headers.set("Accept-Language", "es_ES");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        String[] endpoints = {
                "/api/v1/personal",
                "/api/v1/contact",
                "/api/v1/skills/technical",
                "/api/v1/skills/soft",
                "/api/v1/skills/languages",
                "/api/v1/experience",
                "/api/v1/education",
                "/api/v1/training"
        };

        // When & Then
        for (String endpoint : endpoints) {
            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getHeaders().getFirst("X-Correlation-Id"))
                    .as("Correlation ID for endpoint: " + endpoint)
                    .isEqualTo(correlationId);
        }
    }
}
