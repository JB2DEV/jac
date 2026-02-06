package com.jb2dev.cv.infrastructure.logging.correlation;

import org.slf4j.MDC;

/**
 * Thread-safe holder for correlation IDs using SLF4J's MDC (Mapped Diagnostic Context).
 * <p>
 * This class provides a clean abstraction over MDC, ensuring proper cleanup
 * and avoiding direct MDC usage scattered across the codebase.
 * <p>
 * The correlation ID is automatically included in all log statements within
 * the same thread/request context.
 */
public final class CorrelationIdHolder {

    public static final String CORRELATION_ID_KEY = "correlationId";

    private CorrelationIdHolder() {
        // Utility class - prevent instantiation
    }

    /**
     * Sets the correlation ID for the current thread context.
     *
     * @param correlationId The correlation ID to set
     */
    public static void set(String correlationId) {
        MDC.put(CORRELATION_ID_KEY, correlationId);
    }

    /**
     * Gets the correlation ID from the current thread context.
     *
     * @return The correlation ID, or null if not set
     */
    public static String get() {
        return MDC.get(CORRELATION_ID_KEY);
    }

    /**
     * Removes the correlation ID from the current thread context.
     * Should be called in a finally block to prevent memory leaks.
     */
    public static void clear() {
        MDC.remove(CORRELATION_ID_KEY);
    }
}
