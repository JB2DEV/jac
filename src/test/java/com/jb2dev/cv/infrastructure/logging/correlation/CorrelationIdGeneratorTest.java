package com.jb2dev.cv.infrastructure.logging.correlation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdGeneratorTest {

    private final CorrelationIdGenerator generator = new CorrelationIdGenerator();

    @Test
    void shouldGenerateCorrelationIdWithPrefix() {
        // When
        String correlationId = generator.generate();

        // Then
        assertThat(correlationId).startsWith("req-");
    }

    @Test
    void shouldGenerateUniqueCorrelationIds() {
        // When
        String id1 = generator.generate();
        String id2 = generator.generate();

        // Then
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    void shouldGenerateValidUUID() {
        // When
        String correlationId = generator.generate();
        String uuid = correlationId.replace("req-", "");

        // Then - Should be a valid UUID format
        assertThat(uuid).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
    }
}

