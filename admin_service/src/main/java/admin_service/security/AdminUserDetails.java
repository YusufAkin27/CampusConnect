package admin_service.security;

import admin_service.enums.AdminRole;
import admin_service.enums.Permission;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom UserDetails implementation for admin users.
 * Carries admin-specific information and permission-based authorities.
 */
@Getter
public class AdminUserDetails implements UserDetails {

    private final Long adminId;
    private final String username;
    private final String password;
    private final AdminRole role;
    private final boolean enabled;
    private final Set<GrantedAuthority> authorities;

    public AdminUserDetails(Long adminId, String username, String password,
                            AdminRole role, boolean enabled) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.authorities = buildAuthorities(role);
    }

    private Set<GrantedAuthority> buildAuthorities(AdminRole role) {
        Set<GrantedAuthority> auths = new HashSet<>();
        // Add role-based authority
        auths.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        // Add permission-based authorities
        Set<Permission> permissions = RolePermissionMapping.getPermissions(role);
        for (Permission permission : permissions) {
            auths.add(new SimpleGrantedAuthority(permission.name()));
        }
        return auths;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
