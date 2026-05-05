package friend_service.enums;

/**
 * Represents the status of a friend request lifecycle.
 */
public enum FriendRequestStatus {
    /** The request has been sent and is awaiting response. */
    PENDING,

    /** The receiver has accepted the request. A Friendship will be created. */
    ACCEPTED,

    /** The receiver has rejected the request. */
    REJECTED,

    /** The sender has withdrawn the request before a response. */
    CANCELLED,

    /** The request was not responded to within the allowed time window. */
    EXPIRED
}
