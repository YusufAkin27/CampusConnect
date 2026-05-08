package story_service.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Resolves the current user from API Gateway-propagated headers.
 * The API Gateway is responsible for JWT validation and forwards user information
 * via X-User-Id, X-Username, and X-User-Roles headers.
 */
@Component
public class CurrentUserResolver {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_USER_ROLES = "X-User-Roles";

    /**
     * Resolves current user from HTTP request headers.
     *
     * @param request the HTTP request containing gateway headers
     * @return CurrentUser with extracted user information
     */
    public CurrentUser resolve(HttpServletRequest request) {
        String userIdHeader = request.getHeader(HEADER_USER_ID);
        String username = request.getHeader(HEADER_USERNAME);
        String rolesHeader = request.getHeader(HEADER_USER_ROLES);

        Long userId = null;
        if (userIdHeader != null && !userIdHeader.isBlank()) {
            userId = Long.parseLong(userIdHeader);
        }

        List<String> roles = parseRoles(rolesHeader);

        return CurrentUser.builder()
            .userId(userId)
            .username(username)
            .roles(roles)
            .build();
    }

    /**
     * Validates that the request contains required user identification headers.
     *
     * @param request the HTTP request
     * @return true if required headers are present
     */
    public boolean isAuthenticated(HttpServletRequest request) {
        String userIdHeader = request.getHeader(HEADER_USER_ID);
        return userIdHeader != null && !userIdHeader.isBlank();
    }

    private List<String> parseRoles(String rolesHeader) {
        if (rolesHeader == null || rolesHeader.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(rolesHeader.split(","))
            .map(String::trim)
            .filter(role -> !role.isBlank())
            .collect(Collectors.toList());
    }
}
