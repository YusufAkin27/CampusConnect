package post_service.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import post_service.exception.UnauthorizedUserOperationException;

/**
 * Provides the current authenticated user's authUserId.
 *
 * Currently reads the authUserId from the X-Auth-User-Id header,
 * which is set by the API Gateway after JWT validation.
 *
 * TODO: In production, extract authUserId from JWT claims:
 *   - Inject JwtDecoder or use SecurityContextHolder.getContext().getAuthentication()
 *   - Parse the "sub" or a custom "authUserId" claim from the JWT token
 *   - Example:
 *       Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
 *       return Long.parseLong(jwt.getClaimAsString("authUserId"));
 */
@Component
public class AuthUserProvider {

    private static final String AUTH_USER_ID_HEADER = "X-Auth-User-Id";

    /**
     * Reads the authenticated user's ID from the request header.
     *
     * @param request the current HTTP request
     * @return the authUserId parsed from the header
     * @throws UnauthorizedUserOperationException if the header is missing or invalid
     */
    public Long getCurrentAuthUserId(HttpServletRequest request) {
        String headerValue = request.getHeader(AUTH_USER_ID_HEADER);
        if (headerValue == null || headerValue.isBlank()) {
            throw new UnauthorizedUserOperationException(
                    "Missing required header: " + AUTH_USER_ID_HEADER);
        }
        try {
            return Long.parseLong(headerValue.trim());
        } catch (NumberFormatException e) {
            throw new UnauthorizedUserOperationException(
                    "Invalid " + AUTH_USER_ID_HEADER + " header value: " + headerValue);
        }
    }
}
