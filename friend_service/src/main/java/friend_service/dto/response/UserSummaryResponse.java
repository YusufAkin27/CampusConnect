package friend_service.dto.response;

import lombok.*;

/**
 * Summary of a user's profile, obtained from user-service.
 *
 * When user-service is unavailable or the user is not found,
 * a fallback object is returned with "unknown-user" values.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {

    private Long id;
    private Long authUserId;
    private String username;
    private String displayName;
    private String profileImageUrl;
    private String faculty;
    private String department;
    private String grade;
    private String accountStatus;

    /**
     * Creates a fallback user response when user-service is unavailable.
     */
    public static UserSummaryResponse fallback(Long authUserId) {
        return UserSummaryResponse.builder()
                .authUserId(authUserId)
                .username("unknown-user")
                .displayName("Unknown User")
                .profileImageUrl(null)
                .build();
    }
}
