package event_service.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserContextResolver {

    public UserContext resolve(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");
        String fullName = request.getHeader("X-Full-Name");
        String rolesHeader = request.getHeader("X-User-Roles");

        Long userId = null;
        if (userIdHeader != null && !userIdHeader.isBlank()) {
            userId = Long.parseLong(userIdHeader);
        }

        List<String> roles = parseRoles(rolesHeader);

        return UserContext.builder()
            .userId(userId)
            .username(username)
            .fullName(fullName)
            .roles(roles)
            .build();
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
