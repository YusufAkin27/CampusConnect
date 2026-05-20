package user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.enums.Department;
import user_service.enums.Faculty;
import user_service.enums.Grade;

/**
 * Public profile response - used for public profile view by other users.
 * Contains only publicly visible information; excludes private data like email, phone, studentNumber.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicUserProfileResponse {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String displayName;
    private String bio;
    private String profileImageUrl;

}
