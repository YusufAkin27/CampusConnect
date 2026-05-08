package story_service.security;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUser {

    private Long userId;
    private String username;
    private List<String> roles;

    public boolean isAdmin() {
        if (roles == null) {
            return false;
        }
        return roles.stream().anyMatch(role -> "ADMIN".equalsIgnoreCase(role));
    }

    public List<String> getRolesSafe() {
        return roles == null ? Collections.emptyList() : roles;
    }
}
