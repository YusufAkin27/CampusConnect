package admin_service.repository;

import admin_service.entity.AdminUser;
import admin_service.enums.AdminRole;
import admin_service.enums.AdminStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByUsername(String username);

    Optional<AdminUser> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<AdminUser> findByRole(AdminRole role, Pageable pageable);

    Page<AdminUser> findByStatus(AdminStatus status, Pageable pageable);

    Page<AdminUser> findByRoleAndStatus(AdminRole role, AdminStatus status, Pageable pageable);

    Page<AdminUser> findByFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(
            String fullName, String username, Pageable pageable);
}
