package admin_service.controller;

import admin_service.common.response.DataResponseMessage;
import admin_service.common.response.PagedResponse;
import admin_service.common.response.ResponseMessage;
import admin_service.dto.request.*;
import admin_service.dto.response.AdminUserDetailResponse;
import admin_service.dto.response.AdminUserResponse;
import admin_service.enums.AdminRole;
import admin_service.enums.AdminStatus;
import admin_service.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/admin/admins")
@RequiredArgsConstructor
@Tag(name = "Admin User Management", description = "CRUD operations for admin users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    @Operation(summary = "Create a new admin user")
    public ResponseEntity<DataResponseMessage<AdminUserDetailResponse>> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResponseMessage.success("Admin created successfully.", adminUserService.createAdmin(request)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    @Operation(summary = "List all admin users")
    public ResponseEntity<DataResponseMessage<PagedResponse<AdminUserResponse>>> getAllAdmins(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<AdminUserResponse> result = adminUserService.getAllAdmins(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(DataResponseMessage.success("Admins retrieved.", toPagedResponse(result)));
    }

    @GetMapping("/{adminId}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    @Operation(summary = "Get admin user details")
    public ResponseEntity<DataResponseMessage<AdminUserDetailResponse>> getAdminById(@PathVariable Long adminId) {
        return ResponseEntity.ok(DataResponseMessage.success("Admin retrieved.", adminUserService.getAdminById(adminId)));
    }

    @PutMapping("/{adminId}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    @Operation(summary = "Update admin user")
    public ResponseEntity<DataResponseMessage<AdminUserDetailResponse>> updateAdmin(@PathVariable Long adminId, @Valid @RequestBody UpdateAdminRequest request) {
        return ResponseEntity.ok(DataResponseMessage.success("Admin updated.", adminUserService.updateAdmin(adminId, request)));
    }

    @DeleteMapping("/{adminId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete admin user")
    public ResponseEntity<ResponseMessage> deleteAdmin(@PathVariable Long adminId) {
        adminUserService.deleteAdmin(adminId);
        return ResponseEntity.ok(ResponseMessage.success("Admin deleted successfully."));
    }

    @PatchMapping("/{adminId}/deactivate")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    @Operation(summary = "Deactivate admin user")
    public ResponseEntity<ResponseMessage> deactivateAdmin(@PathVariable Long adminId) {
        adminUserService.deactivateAdmin(adminId);
        return ResponseEntity.ok(ResponseMessage.success("Admin deactivated."));
    }

    @PatchMapping("/{adminId}/role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Change admin role")
    public ResponseEntity<DataResponseMessage<AdminUserDetailResponse>> changeRole(@PathVariable Long adminId, @Valid @RequestBody ChangeRoleRequest request) {
        return ResponseEntity.ok(DataResponseMessage.success("Role changed.", adminUserService.changeRole(adminId, request)));
    }

    @PatchMapping("/{adminId}/password")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE') or #adminId == authentication.principal.adminId")
    @Operation(summary = "Change admin password")
    public ResponseEntity<ResponseMessage> changePassword(@PathVariable Long adminId, @Valid @RequestBody ChangePasswordRequest request) {
        adminUserService.changePassword(adminId, request);
        return ResponseEntity.ok(ResponseMessage.success("Password changed successfully."));
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public ResponseEntity<DataResponseMessage<PagedResponse<AdminUserResponse>>> getByRole(@PathVariable AdminRole role, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<AdminUserResponse> result = adminUserService.getAdminsByRole(role, PageRequest.of(page, size));
        return ResponseEntity.ok(DataResponseMessage.success("Admins by role.", toPagedResponse(result)));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    public ResponseEntity<DataResponseMessage<PagedResponse<AdminUserResponse>>> searchAdmins(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Page<AdminUserResponse> result = adminUserService.searchAdmins(keyword, PageRequest.of(page, size));
        return ResponseEntity.ok(DataResponseMessage.success("Search results.", toPagedResponse(result)));
    }

    private <T> PagedResponse<T> toPagedResponse(Page<T> page) {
        return PagedResponse.of(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}
