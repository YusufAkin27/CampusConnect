package friend_service.enums;

/**
 * Represents the combined social relation status between two users.
 * Used in search results and profile views to show the relationship context.
 */
public enum FriendRelationStatus {
    /** No social relationship exists. */
    NONE,

    /** The two users are confirmed friends. */
    FRIENDS,

    /** The requester has sent a pending friend request to the target. */
    REQUEST_SENT,

    /** The target has sent a pending friend request to the requester. */
    REQUEST_RECEIVED,

    /** The requester follows the target, but they are not friends. */
    FOLLOWING,

    /** The target follows the requester, but they are not friends. */
    FOLLOWED_BY,

    /** Both users follow each other (mutual follow), but are not friends. */
    FOLLOWING_EACH_OTHER
}
