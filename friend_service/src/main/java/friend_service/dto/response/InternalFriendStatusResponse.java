package friend_service.dto.response;

import lombok.*;

/**
 * Lightweight internal response for inter-service communication.
 * Used by post-service, notification-service, etc. to check friend/follow status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalFriendStatusResponse {

    private Long requesterAuthUserId;
    private Long targetAuthUserId;
    private Boolean friends;
    private Boolean following;
    private Boolean followsMe;
    private Long mutualFriendCount;
}
