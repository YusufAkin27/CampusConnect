package user_service.validations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import user_service.dto.request.CreateUserProfileRequest;
import user_service.dto.request.UpdateAccountStatusRequest;
import user_service.dto.request.UpdateProfileImageRequest;
import user_service.dto.request.UpdateUserProfileRequest;

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
    void createUserProfileRequest_ShouldFail_WhenUsernameIsInvalid() {
        CreateUserProfileRequest request = CreateUserProfileRequest.builder()
                .authUserId(1L)
                .username("ab")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("05551234567")
                .build();

        Set<ConstraintViolation<CreateUserProfileRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("username"));
    }

    @Test
    void updateUserProfileRequest_ShouldFail_WhenBioIsTooLong() {
        UpdateUserProfileRequest request = UpdateUserProfileRequest.builder()
                .bio("a".repeat(501))
                .build();

        Set<ConstraintViolation<UpdateUserProfileRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("bio"));
    }

    @Test
    void updateProfileImageRequest_ShouldFail_WhenUrlIsBlank() {
        UpdateProfileImageRequest request = UpdateProfileImageRequest.builder()
                .profileImageUrl(" ")
                .build();

        Set<ConstraintViolation<UpdateProfileImageRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("profileImageUrl"));
    }

    @Test
    void updateAccountStatusRequest_ShouldFail_WhenStatusIsNull() {
        UpdateAccountStatusRequest request = UpdateAccountStatusRequest.builder().accountStatus(null).build();

        Set<ConstraintViolation<UpdateAccountStatusRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("accountStatus"));
    }
}
