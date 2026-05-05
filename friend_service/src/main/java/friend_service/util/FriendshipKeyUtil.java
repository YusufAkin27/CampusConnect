package friend_service.util;

/**
 * Utility class for normalizing user pairs to ensure consistent ordering
 * in Friendship records.
 *
 * By always storing the smaller authUserId as userOne and the larger as userTwo,
 * we guarantee that a single row represents the friendship regardless of
 * which direction the friend request was sent.
 */
public final class FriendshipKeyUtil {

    private FriendshipKeyUtil() {
        // Utility class, no instantiation
    }

    /**
     * Returns the smaller of the two authUserIds (to be stored as userOneAuthUserId).
     */
    public static Long normalizeUserOne(Long firstAuthUserId, Long secondAuthUserId) {
        return Math.min(firstAuthUserId, secondAuthUserId);
    }

    /**
     * Returns the larger of the two authUserIds (to be stored as userTwoAuthUserId).
     */
    public static Long normalizeUserTwo(Long firstAuthUserId, Long secondAuthUserId) {
        return Math.max(firstAuthUserId, secondAuthUserId);
    }
}
