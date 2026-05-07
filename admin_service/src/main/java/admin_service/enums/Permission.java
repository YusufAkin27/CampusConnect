package admin_service.enums;

/**
 * Fine-grained permissions for admin authorization.
 * Used with @PreAuthorize("hasAuthority('PERMISSION_NAME')").
 */
public enum Permission {
    USER_VIEW,
    USER_UPDATE,
    USER_BAN,
    USER_DELETE,
    POST_VIEW,
    POST_DELETE,
    POST_HIDE,
    MEDIA_VIEW,
    MEDIA_DELETE,
    REPORT_VIEW,
    REPORT_RESOLVE,
    LOG_VIEW,
    SYSTEM_MONITOR,
    ADMIN_MANAGE,
    NOTIFICATION_SEND
}
