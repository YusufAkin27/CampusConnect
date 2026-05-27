package auth_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncoderConfigTest {

    @Test
    void passwordEncoder_ShouldReturnBCryptEncoder_AndEncodePasswords() {
        PasswordEncoderConfig config = new PasswordEncoderConfig();

        PasswordEncoder encoder = config.passwordEncoder();
        String encoded = encoder.encode("Password123");

        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
        assertThat(encoded).isNotEqualTo("Password123");
        assertThat(encoder.matches("Password123", encoded)).isTrue();
    }
}

