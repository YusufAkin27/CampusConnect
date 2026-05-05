package friend_service.util;

import friend_service.enums.FriendRelationStatus;
import org.springframework.stereotype.Component;

/**
 * Resolves the combined FriendRelationStatus between a requester and a target
 * based on individual friendship, follow, and request flags.
 */
@Component
public class RelationStatusResolver {

    /**
     * Determines the primary relation status based on available social signals.
     *
     * Priority order:
     * 1. FRIENDS (most significant)
     * 2. REQUEST_SENT / REQUEST_RECEIVED
     * 3. FOLLOWING_EACH_OTHER
     * 4. FOLLOWING / FOLLOWED_BY
     * 5. NONE
     *
     * @param isFriend         true if the two users are confirmed friends
     * @param requestSent      true if requester sent a pending request to target
     * @param requestReceived  true if target sent a pending request to requester
     * @param followingTarget  true if requester follows target
     * @param followedByTarget true if target follows requester
     * @return the resolved FriendRelationStatus
     */
    public FriendRelationStatus resolve(
            boolean isFriend,
            boolean requestSent,
            boolean requestReceived,
            boolean followingTarget,
            boolean followedByTarget
    ) {
        if (isFriend) {
            return FriendRelationStatus.FRIENDS;
        }
        if (requestSent) {
            return FriendRelationStatus.REQUEST_SENT;
        }
        if (requestReceived) {
            return FriendRelationStatus.REQUEST_RECEIVED;
        }
        if (followingTarget && followedByTarget) {
            return FriendRelationStatus.FOLLOWING_EACH_OTHER;
        }
        if (followingTarget) {
            return FriendRelationStatus.FOLLOWING;
        }
        if (followedByTarget) {
            return FriendRelationStatus.FOLLOWED_BY;
        }
        return FriendRelationStatus.NONE;
    }
}
