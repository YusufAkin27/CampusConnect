package friend_service.dto.response;

import friend_service.enums.FollowStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO representing a follow relationship with full follower and following user details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponse {

    private Long id;
    private UserSummaryResponse follower;
    private UserSummaryResponse following;
    private FollowStatus status;
    private LocalDateTime createdAt;
}
