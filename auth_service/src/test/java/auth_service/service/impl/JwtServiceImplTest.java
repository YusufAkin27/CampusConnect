package auth_service.service.impl;

import auth_service.entity.Role;
import auth_service.entity.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "RK487/1UdFpWb0BOUKrlvsuo2Y926qxHqeMmG/yAccUC4kXtulxQPZySXE6z1EkHPpc3/lstozo7bClo3FWcAg==");
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 900_000L);
    }

    @Test
    void generateAccessToken_ShouldContainExpectedClaims_WhenUserIsValid() {
        User user = baseUser();

        String token = jwtService.generateAccessToken(user, Map.of("scope", "api"));

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("john_doe");
        Integer userIdClaim = jwtService.extractClaim(token, claims -> claims.get("userId", Integer.class));
        String emailClaim = jwtService.extractClaim(token, claims -> claims.get("email", String.class));
        String roleClaim = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        String scopeClaim = jwtService.extractClaim(token, claims -> claims.get("scope", String.class));

        assertThat(userIdClaim).isEqualTo(1);
        assertThat(emailClaim).isEqualTo("john@example.com");
        assertThat(roleClaim).isEqualTo("ADMIN");
        assertThat(scopeClaim).isEqualTo("api");
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenMatchesUserAndNotExpired() {
        User user = baseUser();
        String token = jwtService.generateAccessToken(user);

        boolean valid = jwtService.isTokenValid(token, user);

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenTokenBelongsToAnotherUser() {
        User owner = baseUser();
        User anotherUser = User.builder().username("other").build();
        String token = jwtService.generateAccessToken(owner);

        boolean valid = jwtService.isTokenValid(token, anotherUser);

        assertThat(valid).isFalse();
    }

    @Test
    void isTokenExpired_ShouldReturnTrue_WhenTokenIsMalformed() {
        assertThat(jwtService.isTokenExpired("not-a-jwt")).isTrue();
    }

    @Test
    void isTokenExpired_ShouldReturnTrue_WhenExpirationIsAlreadyPast() {
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", -1_000L);
        String token = jwtService.generateAccessToken(baseUser());

        assertThat(jwtService.isTokenExpired(token)).isTrue();
    }

    @Test
    void generateAccessToken_ShouldThrowNullPointerException_WhenUserIsNull() {
        assertThatThrownBy(() -> jwtService.generateAccessToken(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void extractClaim_ShouldReturnClaimValue_WhenResolverIsProvided() {
        String token = jwtService.generateAccessToken(baseUser());

        Long issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt).getTime();

        assertThat(issuedAt).isPositive();
    }

    @Test
    void getAccessTokenExpiration_ShouldReturnConfiguredExpiration() {
        assertThat(jwtService.getAccessTokenExpiration()).isEqualTo(900_000L);
    }

    private User baseUser() {
        return User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .password("encoded")
                .role(Role.ADMIN)
                .enabled(true)
                .accountNonLocked(true)
                .build();
    }
}

