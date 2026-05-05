package friend_service.enums;

/**
 * Sort options available for friend and user listing endpoints.
 */
public enum SortType {
    /** Sort by creation date, newest first. */
    NEWEST,

    /** Sort by creation date, oldest first. */
    OLDEST,

    /** Sort alphabetically by name, A to Z. */
    NAME_ASC,

    /** Sort alphabetically by name, Z to A. */
    NAME_DESC,

    /** Sort by mutual friend count, highest first. */
    MUTUAL_COUNT_DESC
}
