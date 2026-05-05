package friend_service.dto.response;

import lombok.*;

/**
 * Response DTO summarizing a user's social statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialStatsResponse {

    private Long authUserId;
    private Long friendCount;
    private Long followerCount;
    private Long followingCount;
    private Long pendingReceivedRequestCount;
    private Long pendingSentRequestCount;
}
