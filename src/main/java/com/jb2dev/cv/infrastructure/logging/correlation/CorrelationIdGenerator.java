package com.jb2dev.cv.infrastructure.logging.correlation;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Generates unique correlation IDs for request tracking.
 * Uses UUID v4 for guaranteed uniqueness across distributed systems.
 */
@Component
public class CorrelationIdGenerator {

    private static final String CORRELATION_ID_PREFIX = "req-";

    /**
     * Generates a new correlation ID.
     *
     * @return A unique correlation ID with format "req-{uuid}"
     */
    public String generate() {
        return CORRELATION_ID_PREFIX + UUID.randomUUID();
    }
}
