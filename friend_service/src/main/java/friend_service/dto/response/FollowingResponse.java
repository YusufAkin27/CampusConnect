package friend_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for a single following entry with relationship context.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowingResponse {

    private UserSummaryResponse user;
    private Boolean isFriend;
    private Boolean followedByMe;
    private Boolean followsMe;
    private Long mutualFriendCount;
    private LocalDateTime followedAt;
}
