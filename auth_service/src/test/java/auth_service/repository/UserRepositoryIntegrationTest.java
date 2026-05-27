package auth_service.repository;

import auth_service.entity.Role;
import auth_service.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void repositoryMethods_ShouldWorkForUsernameEmailAndExistsChecks() {
        User user = User.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("encoded")
                .role(Role.USER)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        userRepository.save(user);

        Optional<User> byUsername = userRepository.findByUsername("john_doe");
        Optional<User> byEmail = userRepository.findByEmail("john@example.com");
        Optional<User> byEither = userRepository.findByUsernameOrEmail("john_doe", "john_doe");

        assertThat(byUsername).isPresent();
        assertThat(byEmail).isPresent();
        assertThat(byEither).isPresent();
        assertThat(userRepository.existsByUsername("john_doe")).isTrue();
        assertThat(userRepository.existsByEmail("john@example.com")).isTrue();
        assertThat(userRepository.existsByUsername("missing")).isFalse();
    }
}

