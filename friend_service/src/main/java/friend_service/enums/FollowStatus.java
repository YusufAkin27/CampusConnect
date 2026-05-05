package friend_service.enums;

/**
 * Represents the status of a Follow relationship.
 */
public enum FollowStatus {
    /** The follow relationship is currently active. */
    ACTIVE,

    /** The user has unfollowed. Soft-deleted, not physically removed. */
    UNFOLLOWED
}
