package admin_service.service;

import admin_service.dto.request.ChangePasswordRequest;
import admin_service.dto.request.ChangeRoleRequest;
import admin_service.dto.request.CreateAdminRequest;
import admin_service.dto.request.UpdateAdminRequest;
import admin_service.dto.response.AdminUserDetailResponse;
import admin_service.dto.response.AdminUserResponse;
import admin_service.enums.AdminRole;
import admin_service.enums.AdminStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {

    AdminUserDetailResponse createAdmin(CreateAdminRequest request);

    AdminUserDetailResponse updateAdmin(Long adminId, UpdateAdminRequest request);

    void deleteAdmin(Long adminId);

    void deactivateAdmin(Long adminId);

    AdminUserDetailResponse getAdminById(Long adminId);

    AdminUserDetailResponse getAdminByUsername(String username);

    Page<AdminUserResponse> getAllAdmins(Pageable pageable);

    Page<AdminUserResponse> getAdminsByRole(AdminRole role, Pageable pageable);

    Page<AdminUserResponse> getAdminsByStatus(AdminStatus status, Pageable pageable);

    Page<AdminUserResponse> searchAdmins(String keyword, Pageable pageable);

    AdminUserDetailResponse changeRole(Long adminId, ChangeRoleRequest request);

    void changePassword(Long adminId, ChangePasswordRequest request);

    void recordLastLogin(Long adminId);
}
