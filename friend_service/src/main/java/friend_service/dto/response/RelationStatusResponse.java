package friend_service.dto.response;

import friend_service.enums.FriendRelationStatus;
import lombok.*;

/**
 * Response DTO representing the full social relationship context between two users.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationStatusResponse {

    private Long requesterAuthUserId;
    private Long targetAuthUserId;
    private FriendRelationStatus relationStatus;
    private Boolean isFriend;
    private Boolean requestSent;
    private Boolean requestReceived;
    private Boolean following;
    private Boolean followsMe;
    private Long mutualFriendCount;
}
