package comment_service.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * Resolves the current user identity from Gateway-forwarded headers.
 *
 * The API Gateway validates the JWT and forwards user info as HTTP headers:
 * - X-User-Id: the user's unique UUID
 * - X-User-Email: the user's email (optional)
 * - X-User-Role: the user's role (optional)
 */
@Component
public class CurrentUserResolver {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_EMAIL = "X-User-Email";
    private static final String HEADER_USER_ROLE = "X-User-Role";

    /**
     * Extracts the current user's UUID from the X-User-Id header.
     *
     * @return current user's UUID
     * @throws IllegalStateException if the header is missing or invalid
     */
    public UUID getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        String userIdHeader = request.getHeader(HEADER_USER_ID);

        if (userIdHeader == null || userIdHeader.isBlank()) {
            throw new IllegalStateException("X-User-Id header is missing. Authentication required.");
        }

        try {
            return UUID.fromString(userIdHeader);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid X-User-Id header format. Expected UUID.", e);
        }
    }

    /**
     * Extracts the current user's email from the X-User-Email header.
     *
     * @return user email or null if not provided
     */
    public String getCurrentUserEmail() {
        HttpServletRequest request = getCurrentRequest();
        return request.getHeader(HEADER_USER_EMAIL);
    }

    /**
     * Extracts the current user's role from the X-User-Role header.
     *
     * @return user role or null if not provided
     */
    public String getCurrentUserRole() {
        HttpServletRequest request = getCurrentRequest();
        return request.getHeader(HEADER_USER_ROLE);
    }

    /**
     * Checks if the current user has the ADMIN role.
     */
    public boolean isAdmin() {
        String role = getCurrentUserRole();
        return role != null && role.equalsIgnoreCase("ADMIN");
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attributes.getRequest();
    }
}
