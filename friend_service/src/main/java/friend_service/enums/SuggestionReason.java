package friend_service.enums;

/**
 * Reason why a user was suggested in the suggested-users list.
 */
public enum SuggestionReason {
    /** The suggested user is in the same faculty. */
    SAME_FACULTY,

    /** The suggested user is in the same department. */
    SAME_DEPARTMENT,

    /** The suggested user is in the same grade/class year. */
    SAME_GRADE,

    /** The suggested user shares mutual friends with the requester. */
    MUTUAL_FRIENDS,

    /** The suggested user is popular within the university community. */
    POPULAR_IN_UNIVERSITY,

    /** The suggested user recently joined the platform. */
    NEW_USER
}
