package logging_service.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utility for managing traceId, correlationId, and requestId.
 * If an ID is not provided by the upstream service, generates a new UUID.
 */
@Component
public class TraceIdUtil {

    /**
     * Returns the provided traceId, or generates a new UUID if null/blank.
     */
    public String getOrGenerateTraceId(String traceId) {
        if (traceId == null || traceId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return traceId;
    }

    /**
     * Returns the provided correlationId, or generates a new UUID if null/blank.
     */
    public String getOrGenerateCorrelationId(String correlationId) {
        if (correlationId == null || correlationId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return correlationId;
    }

    /**
     * Generates a new unique requestId.
     */
    public String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}
