package media_service.security;

import jakarta.servlet.http.HttpServletRequest;
import media_service.exception.UnauthorizedUserOperationException;
import org.springframework.stereotype.Component;

/**
 * Provides the current authenticated user's authUserId from the request context.
 *
 * In development: reads from the X-Auth-User-Id header set by the API Gateway.
 *
 * TODO (Production): Replace header-based auth with JWT claim extraction.
 *  Example:
 *      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
 *      if (authentication instanceof JwtAuthenticationToken jwtToken) {
 *          return Long.parseLong(jwtToken.getToken().getClaimAsString("authUserId"));
 *      }
 *
 * For internal endpoints, the header may be optional. Use getOptionalAuthUserId() in those cases.
 */
@Component
public class AuthUserProvider {

    public static final String AUTH_USER_ID_HEADER = "X-Auth-User-Id";

    /**
     * Extracts the authUserId from the X-Auth-User-Id request header.
     *
     * @param request the current HTTP request
     * @return the authenticated user's ID
     * @throws UnauthorizedUserOperationException if the header is missing or invalid
     */
    public Long getCurrentAuthUserId(HttpServletRequest request) {
        String headerValue = request.getHeader(AUTH_USER_ID_HEADER);
        if (headerValue == null || headerValue.isBlank()) {
            throw new UnauthorizedUserOperationException(
                    "Authentication required. Missing '" + AUTH_USER_ID_HEADER + "' header.");
        }
        try {
            return Long.parseLong(headerValue.trim());
        } catch (NumberFormatException e) {
            throw new UnauthorizedUserOperationException(
                    "Invalid value for '" + AUTH_USER_ID_HEADER + "' header: " + headerValue);
        }
    }

    /**
     * Returns the authUserId if present, or null if the header is missing.
     * For use in internal endpoints where auth is optional.
     *
     * @param request the current HTTP request
     * @return the authenticated user's ID or null
     */
    public Long getOptionalAuthUserId(HttpServletRequest request) {
        String headerValue = request.getHeader(AUTH_USER_ID_HEADER);
        if (headerValue == null || headerValue.isBlank()) return null;
        try {
            return Long.parseLong(headerValue.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
