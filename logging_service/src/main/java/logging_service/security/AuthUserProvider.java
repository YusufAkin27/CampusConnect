package logging_service.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * Provides the current authenticated user's ID from the request context.
 * Reads the X-Auth-User-Id header set by the API Gateway after JWT validation.
 *
 * Returns null if no user ID is present (for system/anonymous log entries).
 *
 * TODO: In production, validate the header's authenticity using service-to-service trust
 * (e.g., verify it was set by a trusted API Gateway, not directly by clients).
 */
@Component
public class AuthUserProvider {

    private static final String AUTH_USER_ID_HEADER = "X-Auth-User-Id";

    /**
     * Extracts the authenticated user's ID from the request header.
     *
     * @param request the HTTP servlet request
     * @return the auth user ID, or null if not present (anonymous/system calls)
     */
    public Long getCurrentAuthUserId(HttpServletRequest request) {
        String header = request.getHeader(AUTH_USER_ID_HEADER);
        if (header == null || header.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(header.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
