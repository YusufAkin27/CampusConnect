package friend_service.enums;

/**
 * Represents the status of a Friendship relationship.
 */
public enum FriendshipStatus {
    /** The friendship is currently active. */
    ACTIVE,

    /** One party removed the other. Soft-deleted, not physically removed. */
    REMOVED
}
