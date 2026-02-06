package com.jb2dev.cv.infrastructure.logging.correlation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * HTTP filter that ensures every request has a correlation ID for distributed tracing.
 * <p>
 * Behavior:
 * <ul>
 *   <li>Checks for existing X-Correlation-Id header from client/gateway</li>
 *   <li>If not present, generates a new correlation ID</li>
 *   <li>Stores it in MDC for automatic inclusion in all logs</li>
 *   <li>Returns it in response header for client-side correlation</li>
 *   <li>Guarantees cleanup in finally block to prevent memory leaks</li>
 * </ul>
 * <p>
 * This filter executes early in the chain (Order = 1) to ensure correlation ID
 * is available for all subsequent filters, interceptors, and application logic.
 */
@Slf4j
@RequiredArgsConstructor
@Component
@Order(1)
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    private final CorrelationIdGenerator correlationIdGenerator;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String correlationId = extractOrGenerateCorrelationId(request);

        try {
            CorrelationIdHolder.set(correlationId);
            response.setHeader(CORRELATION_ID_HEADER, correlationId);

            log.debug("Request started: {} {}", request.getMethod(), request.getRequestURI());

            filterChain.doFilter(request, response);

            log.debug("Request completed: {} {} - Status: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus());

        } finally {
            // Critical: Always clear MDC to prevent memory leaks in thread pools
            CorrelationIdHolder.clear();
        }
    }

    private String extractOrGenerateCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = correlationIdGenerator.generate();
            log.trace("Generated new correlation ID: {}", correlationId);
        } else {
            log.trace("Using existing correlation ID from header: {}", correlationId);
        }

        return correlationId;
    }
}
