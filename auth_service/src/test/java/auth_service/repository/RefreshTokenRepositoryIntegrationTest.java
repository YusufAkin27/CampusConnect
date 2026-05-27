package auth_service.repository;

import auth_service.entity.RefreshToken;
import auth_service.entity.Role;
import auth_service.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RefreshTokenRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void revokeAllUserTokens_ShouldMarkAllActiveTokensAsRevoked() {
        User user = userRepository.save(User.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("encoded")
                .role(Role.USER)
                .enabled(true)
                .accountNonLocked(true)
                .build());

        RefreshToken token1 = refreshTokenRepository.save(RefreshToken.builder()
                .token("token-1")
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .revoked(false)
                .build());

        refreshTokenRepository.save(RefreshToken.builder()
                .token("token-2")
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .revoked(false)
                .build());

        refreshTokenRepository.revokeAllUserTokens(user);
        entityManager.flush();
        entityManager.clear();

        RefreshToken refreshed1 = refreshTokenRepository.findById(token1.getId()).orElseThrow();
        RefreshToken refreshed2 = refreshTokenRepository.findByToken("token-2").orElseThrow();

        assertThat(refreshed1.isRevoked()).isTrue();
        assertThat(refreshed2.isRevoked()).isTrue();
    }

    @Test
    void deleteAllByUser_ShouldDeleteUserTokens() {
        User user = userRepository.save(User.builder()
                .username("alice")
                .email("alice@example.com")
                .password("encoded")
                .role(Role.USER)
                .enabled(true)
                .accountNonLocked(true)
                .build());

        refreshTokenRepository.save(RefreshToken.builder()
                .token("alice-token")
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .revoked(false)
                .build());

        refreshTokenRepository.deleteAllByUser(user);

        assertThat(refreshTokenRepository.findByToken("alice-token")).isEmpty();
    }
}

