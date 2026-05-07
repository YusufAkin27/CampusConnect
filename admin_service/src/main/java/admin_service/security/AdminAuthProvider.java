package admin_service.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Component for resolving the current authenticated admin user's ID.
 * First tries the JWT-based SecurityContext, then falls back to X-Admin-Id header.
 */
@Component
@Slf4j
public class AdminAuthProvider {

    private static final String ADMIN_ID_HEADER = "X-Admin-Id";

    /**
     * Returns the current authenticated admin user's ID.
     *
     * @return admin user ID
     * @throws IllegalStateException if admin ID cannot be resolved
     */
    public Long getCurrentAdminId() {
        // Strategy 1: Try from SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AdminUserDetails adminDetails) {
            log.debug("Resolved adminId from SecurityContext: {}", adminDetails.getAdminId());
            return adminDetails.getAdminId();
        }

        // Strategy 2: Fallback to X-Admin-Id header
        return getAdminIdFromHeader();
    }

    private Long getAdminIdFromHeader() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            throw new IllegalStateException("No request context available.");
        }

        HttpServletRequest request = attrs.getRequest();
        String headerValue = request.getHeader(ADMIN_ID_HEADER);

        if (headerValue == null || headerValue.isBlank()) {
            log.warn("X-Admin-Id header is missing from the request.");
            throw new IllegalStateException("Authentication required. X-Admin-Id header is missing.");
        }

        try {
            Long adminId = Long.parseLong(headerValue.trim());
            log.debug("Resolved adminId from header: {}", adminId);
            return adminId;
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid X-Admin-Id header value: " + headerValue);
        }
    }
}
