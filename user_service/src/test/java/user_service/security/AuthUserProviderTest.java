package user_service.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import user_service.exception.UnauthorizedUserOperationException;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthUserProviderTest {

    private final AuthUserProvider authUserProvider = new AuthUserProvider();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getCurrentAuthUserId_ShouldReturnIdFromJwtClaim_WhenJwtPresent() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("authUserId", 42)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(jwt, null));

        Long result = authUserProvider.getCurrentAuthUserId();

        assertThat(result).isEqualTo(42L);
    }

    @Test
    void getCurrentAuthUserId_ShouldFallbackToHeader_WhenJwtMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Auth-User-Id", "99");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Long result = authUserProvider.getCurrentAuthUserId();

        assertThat(result).isEqualTo(99L);
    }

    @Test
    void getCurrentAuthUserId_ShouldThrow_WhenHeaderIsMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertThatThrownBy(() -> authUserProvider.getCurrentAuthUserId())
                .isInstanceOf(UnauthorizedUserOperationException.class)
                .hasMessageContaining("X-Auth-User-Id");
    }

    @Test
    void getCurrentAuthUserId_ShouldThrow_WhenHeaderIsInvalid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Auth-User-Id", "not-a-number");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertThatThrownBy(() -> authUserProvider.getCurrentAuthUserId())
                .isInstanceOf(UnauthorizedUserOperationException.class)
                .hasMessageContaining("Invalid X-Auth-User-Id");
    }

    @Test
    void getCurrentAuthUserId_ShouldThrow_WhenNoRequestContextAvailable() {
        RequestContextHolder.resetRequestAttributes();

        assertThatThrownBy(() -> authUserProvider.getCurrentAuthUserId())
                .isInstanceOf(UnauthorizedUserOperationException.class)
                .hasMessageContaining("No request context");
    }
}
