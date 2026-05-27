package auth_service.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    @Test
    void getAuthorities_ShouldContainRolePrefix_WhenRoleIsAdmin() {
        User user = User.builder()
                .username("admin")
                .email("admin@example.com")
                .password("encoded")
                .role(Role.ADMIN)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        assertThat(user.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }
}

