package friend_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for a single follower entry with relationship context.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerResponse {

    private UserSummaryResponse user;
    private Boolean isFriend;
    private Boolean followedByMe;
    private Boolean followsMe;
    private Long mutualFriendCount;
    private LocalDateTime followedAt;
}
