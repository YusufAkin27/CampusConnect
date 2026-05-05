package user_service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.enums.Department;
import user_service.enums.Faculty;
import user_service.enums.Gender;
import user_service.enums.Grade;
import user_service.enums.ProfileVisibility;

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

    @Size(max = 100, message = "Display name cannot exceed 100 characters")
    private String displayName;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    private LocalDate birthDate;

    private Gender gender;

    private Faculty faculty;

    private Department department;

    private Grade grade;

    @Size(max = 20, message = "Student number cannot exceed 20 characters")
    private String studentNumber;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    @Size(max = 255, message = "Website URL cannot exceed 255 characters")
    private String websiteUrl;

    @Size(max = 255, message = "Instagram URL cannot exceed 255 characters")
    private String instagramUrl;

    @Size(max = 255, message = "LinkedIn URL cannot exceed 255 characters")
    private String linkedinUrl;

    @Size(max = 255, message = "GitHub URL cannot exceed 255 characters")
    private String githubUrl;

    private ProfileVisibility profileVisibility;
}
