package admin_service.controller;

import admin_service.common.response.DataResponseMessage;
import admin_service.common.response.ResponseMessage;
import admin_service.dto.request.BanUserRequest;
import admin_service.dto.request.UpdateUserStatusRequest;
import admin_service.dto.response.UserBanRecordResponse;
import admin_service.security.AdminAuthProvider;
import admin_service.service.UserAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "User Administration", description = "Manage platform users from admin panel")
public class UserAdminController {

    private final UserAdminService userAdminService;
    private final AdminAuthProvider adminAuthProvider;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_VIEW')")
    @Operation(summary = "List all users")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(DataResponseMessage.success("Users retrieved.", userAdminService.getAllUsers(page, size)));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    @Operation(summary = "Get user details")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(DataResponseMessage.success("User retrieved.", userAdminService.getUserById(userId)));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    @Operation(summary = "Search users")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> searchUsers(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(DataResponseMessage.success("Search results.", userAdminService.searchUsers(keyword, page, size)));
    }

    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @Operation(summary = "Update user status")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> updateStatus(@PathVariable Long userId, @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(DataResponseMessage.success("Status updated.", userAdminService.updateUserStatus(userId, request)));
    }

    @PatchMapping("/{userId}/ban")
    @PreAuthorize("hasAuthority('USER_BAN')")
    @Operation(summary = "Ban a user")
    public ResponseEntity<DataResponseMessage<UserBanRecordResponse>> banUser(@PathVariable Long userId, @Valid @RequestBody BanUserRequest request) {
        Long adminId = adminAuthProvider.getCurrentAdminId();
        return ResponseEntity.ok(DataResponseMessage.success("User banned.", userAdminService.banUser(userId, request, adminId)));
    }

    @PatchMapping("/{userId}/unban")
    @PreAuthorize("hasAuthority('USER_BAN')")
    @Operation(summary = "Unban a user")
    public ResponseEntity<DataResponseMessage<UserBanRecordResponse>> unbanUser(@PathVariable Long userId) {
        Long adminId = adminAuthProvider.getCurrentAdminId();
        return ResponseEntity.ok(DataResponseMessage.success("User unbanned.", userAdminService.unbanUser(userId, adminId)));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @Operation(summary = "Delete a user")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(DataResponseMessage.success("User deleted.", userAdminService.deleteUser(userId)));
    }

    @GetMapping("/{userId}/posts")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getUserPosts(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(DataResponseMessage.success("User posts.", userAdminService.getUserPosts(userId, page, size)));
    }

    @GetMapping("/{userId}/friends")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getUserFriends(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(DataResponseMessage.success("User friends.", userAdminService.getUserFriends(userId, page, size)));
    }

    @GetMapping("/{userId}/reports")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<DataResponseMessage<List<UserBanRecordResponse>>> getUserBanHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(DataResponseMessage.success("Ban history.", userAdminService.getUserBanHistory(userId)));
    }

    @GetMapping("/{userId}/activity")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getUserActivity(@PathVariable Long userId) {
        return ResponseEntity.ok(DataResponseMessage.success("User activity.", userAdminService.getUserActivity(userId)));
    }
}
