package auth_service.validations;

import auth_service.dto.request.ChangePasswordRequest;
import auth_service.dto.request.LoginRequest;
import auth_service.dto.request.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void registerRequest_ShouldFailValidation_WhenEmailIsInvalid() {
        RegisterRequest request = RegisterRequest.builder()
                .username("john_doe")
                .email("invalid-email")
                .password("Password123")
                .build();

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void loginRequest_ShouldFailValidation_WhenUsernameOrEmailIsInvalid() {
        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail("ab")
                .password("Password123")
                .build();

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("usernameOrEmail"));
    }

    @Test
    void changePasswordRequest_ShouldFailValidation_WhenNewPasswordIsWeak() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("Current123")
                .newPassword("weak")
                .confirmPassword("weak")
                .build();

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("newPassword"));
    }

    @Test
    void registerRequest_ShouldFailValidation_WhenNullFieldsProvided() {
        RegisterRequest request = new RegisterRequest(null, null, null);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(3);
    }
}

