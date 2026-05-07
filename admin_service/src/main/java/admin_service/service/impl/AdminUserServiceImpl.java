package admin_service.service.impl;

import admin_service.dto.request.ChangePasswordRequest;
import admin_service.dto.request.ChangeRoleRequest;
import admin_service.dto.request.CreateAdminRequest;
import admin_service.dto.request.UpdateAdminRequest;
import admin_service.dto.response.AdminUserDetailResponse;
import admin_service.dto.response.AdminUserResponse;
import admin_service.entity.AdminUser;
import admin_service.enums.ActionType;
import admin_service.enums.AdminRole;
import admin_service.enums.AdminStatus;
import admin_service.enums.TargetType;
import admin_service.exception.AdminAlreadyExistsException;
import admin_service.exception.AdminNotFoundException;
import admin_service.mapper.AdminUserMapper;
import admin_service.repository.AdminUserRepository;
import admin_service.security.AdminAction;
import admin_service.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @AdminAction(actionType = ActionType.ADMIN_CREATED, targetType = TargetType.USER)
    public AdminUserDetailResponse createAdmin(CreateAdminRequest request) {
        if (adminUserRepository.existsByUsername(request.getUsername())) {
            throw new AdminAlreadyExistsException("Admin with username '" + request.getUsername() + "' already exists.");
        }
        if (adminUserRepository.existsByEmail(request.getEmail())) {
            throw new AdminAlreadyExistsException("Admin with email '" + request.getEmail() + "' already exists.");
        }

        AdminUser admin = AdminUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole())
                .status(AdminStatus.ACTIVE)
                .build();

        AdminUser saved = adminUserRepository.save(admin);
        log.info("Admin user created: {} with role {}", saved.getUsername(), saved.getRole());
        return adminUserMapper.toDetailResponse(saved);
    }

    @Override
    @AdminAction(actionType = ActionType.ADMIN_UPDATED, targetType = TargetType.USER)
    public AdminUserDetailResponse updateAdmin(Long adminId, UpdateAdminRequest request) {
        AdminUser admin = findAdminOrThrow(adminId);

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            admin.setFullName(request.getFullName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!admin.getEmail().equals(request.getEmail()) && adminUserRepository.existsByEmail(request.getEmail())) {
                throw new AdminAlreadyExistsException("Email '" + request.getEmail() + "' is already in use.");
            }
            admin.setEmail(request.getEmail());
        }

        AdminUser updated = adminUserRepository.save(admin);
        log.info("Admin user updated: {}", updated.getUsername());
        return adminUserMapper.toDetailResponse(updated);
    }

    @Override
    @AdminAction(actionType = ActionType.ADMIN_DELETED, targetType = TargetType.USER)
    public void deleteAdmin(Long adminId) {
        AdminUser admin = findAdminOrThrow(adminId);
        adminUserRepository.delete(admin);
        log.info("Admin user deleted: {}", admin.getUsername());
    }

    @Override
    public void deactivateAdmin(Long adminId) {
        AdminUser admin = findAdminOrThrow(adminId);
        admin.setStatus(AdminStatus.PASSIVE);
        adminUserRepository.save(admin);
        log.info("Admin user deactivated: {}", admin.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserDetailResponse getAdminById(Long adminId) {
        return adminUserMapper.toDetailResponse(findAdminOrThrow(adminId));
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserDetailResponse getAdminByUsername(String username) {
        AdminUser admin = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with username: " + username));
        return adminUserMapper.toDetailResponse(admin);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getAllAdmins(Pageable pageable) {
        return adminUserRepository.findAll(pageable).map(adminUserMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getAdminsByRole(AdminRole role, Pageable pageable) {
        return adminUserRepository.findByRole(role, pageable).map(adminUserMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getAdminsByStatus(AdminStatus status, Pageable pageable) {
        return adminUserRepository.findByStatus(status, pageable).map(adminUserMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> searchAdmins(String keyword, Pageable pageable) {
        return adminUserRepository
                .findByFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(keyword, keyword, pageable)
                .map(adminUserMapper::toResponse);
    }

    @Override
    @AdminAction(actionType = ActionType.ADMIN_ROLE_CHANGED, targetType = TargetType.USER)
    public AdminUserDetailResponse changeRole(Long adminId, ChangeRoleRequest request) {
        AdminUser admin = findAdminOrThrow(adminId);
        log.info("Changing role of admin {} from {} to {}", admin.getUsername(), admin.getRole(), request.getRole());
        admin.setRole(request.getRole());
        AdminUser updated = adminUserRepository.save(admin);
        return adminUserMapper.toDetailResponse(updated);
    }

    @Override
    @AdminAction(actionType = ActionType.ADMIN_PASSWORD_CHANGED, targetType = TargetType.USER)
    public void changePassword(Long adminId, ChangePasswordRequest request) {
        AdminUser admin = findAdminOrThrow(adminId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        adminUserRepository.save(admin);
        log.info("Password changed for admin: {}", admin.getUsername());
    }

    @Override
    public void recordLastLogin(Long adminId) {
        AdminUser admin = findAdminOrThrow(adminId);
        admin.setLastLoginAt(LocalDateTime.now());
        adminUserRepository.save(admin);
    }

    private AdminUser findAdminOrThrow(Long adminId) {
        return adminUserRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with id: " + adminId));
    }
}
