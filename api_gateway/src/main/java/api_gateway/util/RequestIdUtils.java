package api_gateway.util;

import java.util.UUID;

public final class RequestIdUtils {

    private RequestIdUtils() {
    }

    public static String resolveRequestId(String header) {
        return header == null || header.isBlank() ? UUID.randomUUID().toString() : header;
    }

    public static String resolveCorrelationId(String header, String requestId) {
        return header == null || header.isBlank() ? requestId : header;
    }
}
