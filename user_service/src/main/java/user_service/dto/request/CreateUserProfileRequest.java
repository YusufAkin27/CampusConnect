package user_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.validations.ValidPhoneNumber;
import user_service.validations.ValidUsername;

/**
 * Request DTO for creating a new user profile.
 * Typically called by auth-service after successful user registration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserProfileRequest {

    @NotNull(message = "Auth user ID cannot be null")
    private Long authUserId;

    @NotBlank(message = "Username cannot be blank")
    @ValidUsername
    private String username;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @ValidPhoneNumber
    private String phoneNumber;

}
