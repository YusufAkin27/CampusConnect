package user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Full user profile response - used for authenticated user's own profile view.
 * Contains all profile information including private details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private Long authUserId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String displayName;
    private String bio;
    private String profileImageUrl;
    private String coverImageUrl;
    private String phoneNumber;
    private LocalDate birthDate;
    private Gender gender;
    private Faculty faculty;
    private Department department;
    private Grade grade;
    private String studentNumber;
    private String location;
    private String websiteUrl;
    private String instagramUrl;
    private String linkedinUrl;
    private String githubUrl;
    private ProfileVisibility profileVisibility;
    private AccountStatus accountStatus;
    private Boolean profileCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
