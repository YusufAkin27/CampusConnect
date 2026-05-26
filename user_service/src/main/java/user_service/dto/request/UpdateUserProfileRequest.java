package user_service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.enums.ProfileVisibility;
import user_service.validations.ValidPhoneNumber;

import java.time.LocalDate;

/**
 * Request DTO for updating user profile.
 * All fields are optional - only non-null fields will be updated.
 * Username and email cannot be changed via this endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequest {

    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    @ValidPhoneNumber
    private String phoneNumber;

    private ProfileVisibility profileVisibility;
}
