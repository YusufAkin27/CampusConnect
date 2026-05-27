package auth_service.validations;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CustomValidatorsTest {

    @Nested
    class EmailValidatorTests {
        private final EmailValidator validator = new EmailValidator();

        @ParameterizedTest
        @ValueSource(strings = {"a@b.com", "john.doe+1@example.co", "user_123@test-domain.org"})
        void isValid_ShouldReturnTrue_ForValidEmails(String email) {
            assertThat(validator.isValid(email, null)).isTrue();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "abc", "abc@", "@test.com", "a@@b.com"})
        void isValid_ShouldReturnFalse_ForInvalidEmails(String email) {
            assertThat(validator.isValid(email, null)).isFalse();
        }
    }

    @Nested
    class PasswordValidatorTests {
        private final PasswordValidator validator = new PasswordValidator();

        @ParameterizedTest
        @ValueSource(strings = {"Password123", "abc12345", "A1b2c3d4e5"})
        void isValid_ShouldReturnTrue_ForValidPasswords(String password) {
            assertThat(validator.isValid(password, null)).isTrue();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "12345678", "abcdefgh", "short1", "onlyLettersPassword"})
        void isValid_ShouldReturnFalse_ForInvalidPasswords(String password) {
            assertThat(validator.isValid(password, null)).isFalse();
        }
    }

    @Nested
    class UsernameValidatorTests {
        private final UsernameValidator validator = new UsernameValidator();

        @ParameterizedTest
        @ValueSource(strings = {"abc", "user_name", "User123", "A1234567890"})
        void isValid_ShouldReturnTrue_ForValidUsernames(String username) {
            assertThat(validator.isValid(username, null)).isTrue();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "ab", "user-name", "contains space", "@username"})
        void isValid_ShouldReturnFalse_ForInvalidUsernames(String username) {
            assertThat(validator.isValid(username, null)).isFalse();
        }

        @Test
        void isValid_ShouldReturnFalse_WhenUsernameLengthIsGreaterThan50() {
            String username = "a".repeat(51);

            assertThat(validator.isValid(username, null)).isFalse();
        }
    }

    @Nested
    class LoginFieldValidatorTests {
        private final LoginFieldValidator validator = new LoginFieldValidator();

        @ParameterizedTest
        @ValueSource(strings = {"john_doe", "john@example.com"})
        void isValid_ShouldReturnTrue_ForValidLoginField(String value) {
            assertThat(validator.isValid(value, null)).isTrue();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "ab", "bad email@", "invalid-user!"})
        void isValid_ShouldReturnFalse_ForInvalidLoginField(String value) {
            assertThat(validator.isValid(value, null)).isFalse();
        }
    }
}

