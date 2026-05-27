package user_service.validations;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CustomValidatorsTest {

    @Nested
    class UsernameValidatorTests {
        private final UsernameValidator validator = new UsernameValidator();

        @ParameterizedTest
        @ValueSource(strings = {"abc", "user_name", "User123"})
        void isValid_ShouldReturnTrue_ForValidUsernames(String username) {
            assertThat(validator.isValid(username, null)).isTrue();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "ab", "user-name", "bad user", "@user"})
        void isValid_ShouldReturnFalse_ForInvalidUsernames(String username) {
            assertThat(validator.isValid(username, null)).isFalse();
        }
    }

    @Nested
    class PhoneNumberValidatorTests {
        private final PhoneNumberValidator validator = new PhoneNumberValidator();

        @ParameterizedTest
        @ValueSource(strings = {"05551234567", "+905551234567", "05551234567"})
        void isValid_ShouldReturnTrue_ForValidPhones(String phone) {
            assertThat(validator.isValid(phone, null)).isTrue();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "123", "0555123456", "+1-555-123"})
        void isValid_ShouldReturnFalse_ForInvalidPhones(String phone) {
            assertThat(validator.isValid(phone, null)).isFalse();
        }
    }
}
