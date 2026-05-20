package user_service.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import user_service.exception.UnauthorizedUserOperationException;

@Component
@Slf4j
public class AuthUserProvider {

    private static final String AUTH_USER_ID_HEADER = "X-Auth-User-Id";


    public Long getCurrentAuthUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                Object authUserIdClaim = jwt.getClaim("authUserId");
                if (authUserIdClaim != null) {
                    log.debug("Resolved authUserId from JWT claim: {}", authUserIdClaim);
                    return Long.valueOf(authUserIdClaim.toString());
                }
            }
        } catch (Exception e) {
            log.debug("Could not resolve authUserId from JWT, falling back to header: {}", e.getMessage());
        }

        return getAuthUserIdFromHeader();
    }

    /**
     * Reads authUserId from the X-Auth-User-Id request header.
     * Used as a fallback or in development mode.
     */
    private Long getAuthUserIdFromHeader() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new UnauthorizedUserOperationException("No request context available.");
        }

        HttpServletRequest request = attributes.getRequest();
        String headerValue = request.getHeader(AUTH_USER_ID_HEADER);

        if (headerValue == null || headerValue.isBlank()) {
            log.warn("X-Auth-User-Id header is missing from the request.");
            throw new UnauthorizedUserOperationException("Authentication required. X-Auth-User-Id header is missing.");
        }

        try {
            Long authUserId = Long.parseLong(headerValue.trim());
            log.debug("Resolved authUserId from header: {}", authUserId);
            return authUserId;
        } catch (NumberFormatException e) {
            throw new UnauthorizedUserOperationException("Invalid X-Auth-User-Id header value: " + headerValue);
        }
    }
}
