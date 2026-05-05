package friend_service.dto.response;

import friend_service.enums.FriendRelationStatus;
import lombok.*;

/**
 * Enriched user result combining user profile info with social relation context.
 * Used in search results, suggested users, and follower/following lists.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRelationResponse {

    private UserSummaryResponse user;
    private FriendRelationStatus relationStatus;
    private Boolean isFriend;
    private Boolean requestSent;
    private Boolean requestReceived;
    private Boolean following;
    private Boolean followedByMe;
    private Boolean followsMe;
    private Long mutualFriendCount;
}
