package admin_service.security;

import admin_service.enums.AdminRole;
import admin_service.enums.Permission;

import java.util.*;

/**
 * Static mapping of AdminRole to their granted permissions.
 * Used by security filters and @PreAuthorize checks.
 */
public final class RolePermissionMapping {

    private static final Map<AdminRole, Set<Permission>> ROLE_PERMISSIONS = new EnumMap<>(AdminRole.class);

    static {
        // SUPER_ADMIN: All permissions
        ROLE_PERMISSIONS.put(AdminRole.SUPER_ADMIN, EnumSet.allOf(Permission.class));

        // ADMIN: Almost all permissions except ADMIN_MANAGE
        ROLE_PERMISSIONS.put(AdminRole.ADMIN, EnumSet.of(
                Permission.USER_VIEW, Permission.USER_UPDATE, Permission.USER_BAN, Permission.USER_DELETE,
                Permission.POST_VIEW, Permission.POST_DELETE, Permission.POST_HIDE,
                Permission.MEDIA_VIEW, Permission.MEDIA_DELETE,
                Permission.REPORT_VIEW, Permission.REPORT_RESOLVE,
                Permission.LOG_VIEW, Permission.SYSTEM_MONITOR,
                Permission.NOTIFICATION_SEND
        ));

        // MODERATOR: User, post, and report management
        ROLE_PERMISSIONS.put(AdminRole.MODERATOR, EnumSet.of(
                Permission.USER_VIEW, Permission.USER_UPDATE, Permission.USER_BAN,
                Permission.POST_VIEW, Permission.POST_DELETE, Permission.POST_HIDE,
                Permission.REPORT_VIEW, Permission.REPORT_RESOLVE,
                Permission.LOG_VIEW
        ));

        // SUPPORT: Read-only user details and support tickets
        ROLE_PERMISSIONS.put(AdminRole.SUPPORT, EnumSet.of(
                Permission.USER_VIEW,
                Permission.REPORT_VIEW,
                Permission.LOG_VIEW
        ));

        // CONTENT_MANAGER: Post and media management
        ROLE_PERMISSIONS.put(AdminRole.CONTENT_MANAGER, EnumSet.of(
                Permission.POST_VIEW, Permission.POST_DELETE, Permission.POST_HIDE,
                Permission.MEDIA_VIEW, Permission.MEDIA_DELETE
        ));
    }

    private RolePermissionMapping() {
        // Utility class
    }

    /**
     * Returns the set of permissions for the given admin role.
     */
    public static Set<Permission> getPermissions(AdminRole role) {
        return ROLE_PERMISSIONS.getOrDefault(role, Collections.emptySet());
    }

    /**
     * Checks if the given role has the specified permission.
     */
    public static boolean hasPermission(AdminRole role, Permission permission) {
        return getPermissions(role).contains(permission);
    }
}
