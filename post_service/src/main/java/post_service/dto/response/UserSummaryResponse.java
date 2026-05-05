package post_service.dto.response;

import lombok.*;

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

    /**
     * Returns a fallback UserSummaryResponse when user-service is unavailable.
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
