package user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import user_service.common.response.DataResponseMessage;
import user_service.common.response.PageResponse;
import user_service.common.response.ResponseMessage;
import user_service.dto.request.*;
import user_service.dto.response.*;

import user_service.service.UserProfileService;


@RestController
@RequestMapping("/v1/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Profile API", description = "Endpoints for creating, reading, updating, and managing user profiles in CampusConnect.")
public class UserProfileController {

    private final UserProfileService userProfileService;


    @Operation(
            summary = "Create user profile",
            description = "Creates a user profile after successful registration in auth-service. " +
                          "This endpoint should be called by auth-service internally after a new user registers."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Duplicate username, email, or student number")
    })
    @PostMapping("/profiles")
    public ResponseEntity<DataResponseMessage<UserProfileResponse>> createProfile(
            @Valid @RequestBody CreateUserProfileRequest request) {
        log.info("POST /v1/api/users/profiles - Creating profile for authUserId: {}", request.getAuthUserId());
        DataResponseMessage<UserProfileResponse> response = userProfileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(
            summary = "Get my profile",
            description = "Returns the full profile of the currently authenticated user. " +
                          "The authUserId is resolved from the X-Auth-User-Id header."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/me")
    public ResponseEntity<DataResponseMessage<UserProfileResponse>> getMyProfile(
            @Parameter(description = "Authenticated user's auth ID (set by API Gateway)", required = true)
            @RequestHeader("X-Auth-User-Id") Long authUserId) {
        log.debug("GET /v1/api/users/me - authUserId: {}", authUserId);
        return ResponseEntity.ok(userProfileService.getMyProfile(authUserId));
    }

    @Operation(
            summary = "Update my profile",
            description = "Updates the authenticated user's profile. Only non-null fields are updated. " +
                          "Username and email cannot be changed via this endpoint."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "409", description = "Duplicate student number")
    })
    @PutMapping("/me")
    public ResponseEntity<DataResponseMessage<UserProfileResponse>> updateMyProfile(
            @RequestHeader("X-Auth-User-Id") Long authUserId,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        log.info("PUT /v1/api/users/me - authUserId: {}", authUserId);
        return ResponseEntity.ok(userProfileService.updateMyProfile(authUserId, request));
    }

    @Operation(
            summary = "Update profile image",
            description = "Updates the authenticated user's profile image URL. " +
                          "The actual image upload is handled by file-service; this stores the resulting URL."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile image updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid image URL"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PatchMapping("/me/profile-image")
    public ResponseEntity<DataResponseMessage<UserProfileResponse>> updateProfileImage(
            @RequestHeader("X-Auth-User-Id") Long authUserId,
            @Valid @RequestBody UpdateProfileImageRequest request) {
        log.info("PATCH /v1/api/users/me/profile-image - authUserId: {}", authUserId);
        return ResponseEntity.ok(userProfileService.updateProfileImage(authUserId, request));
    }


    @Operation(
            summary = "Deactivate my account",
            description = "Sets the authenticated user's account status to PASSIVE. " +
                          "The user will no longer appear in search results."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account deactivated successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @PatchMapping("/me/deactivate")
    public ResponseEntity<ResponseMessage> deactivateMyProfile(
            @RequestHeader("X-Auth-User-Id") Long authUserId) {
        log.info("PATCH /v1/api/users/me/deactivate - authUserId: {}", authUserId);
        return ResponseEntity.ok(userProfileService.deactivateMyProfile(authUserId));
    }

    @Operation(
            summary = "Check profile completion",
            description = "Returns the profile completion percentage and a list of missing required fields."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile completion status retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/me/completion")
    public ResponseEntity<DataResponseMessage<ProfileCompletionResponse>> checkProfileCompletion(
            @RequestHeader("X-Auth-User-Id") Long authUserId) {
        log.debug("GET /v1/api/users/me/completion - authUserId: {}", authUserId);
        return ResponseEntity.ok(userProfileService.checkProfileCompletion(authUserId));
    }

    @Operation(
            summary = "Get public profile by username",
            description = "Returns the public profile for a given username. " +
                          "Throws 403 if the profile is set to PRIVATE. " +
                          "Throws 403 if the user is not ACTIVE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Public profile retrieved"),
            @ApiResponse(responseCode = "403", description = "Profile is private or user is inactive"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<DataResponseMessage<PublicUserProfileResponse>> getPublicProfile(
            @Parameter(description = "Username of the target user", required = true)
            @PathVariable String username) {
        log.debug("GET /v1/api/users/username/{}", username);
        return ResponseEntity.ok(userProfileService.getPublicProfileByUsername(username));
    }


    @Operation(
            summary = "Get profile by ID",
            description = "Returns a user profile by its profile ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DataResponseMessage<UserProfileResponse>> getProfileById(
            @Parameter(description = "Profile ID", required = true)
            @PathVariable Long id) {
        log.debug("GET /v1/api/users/{}", id);
        return ResponseEntity.ok(userProfileService.getProfileById(id));
    }

    @Operation(
            summary = "Get profile by auth user ID",
            description = "Returns a user profile by the auth-service user ID (authUserId)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/auth/{authUserId}")
    public ResponseEntity<DataResponseMessage<UserProfileResponse>> getProfileByAuthUserId(
            @Parameter(description = "Auth service user ID", required = true)
            @PathVariable Long authUserId) {
        log.debug("GET /v1/api/users/auth/{}", authUserId);
        return ResponseEntity.ok(userProfileService.getProfileByAuthUserId(authUserId));
    }

    @Operation(
            summary = "Search users",
            description = "Searches active users by keyword across username, name, and email fields. " +
                          "Supports optional filters for faculty, department, and grade. Returns paginated results."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping("/search")
    public ResponseEntity<DataResponseMessage<PageResponse<UserSummaryResponse>>> searchUsers(
            @Parameter(description = "Search keyword (username, first name, last name, display name, email)")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {

        log.debug("GET /v1/api/users/search - keyword: {}, faculty: {}, department: {}, grade: {}, page: {}, size: {}",
                keyword,  page, size);
        return ResponseEntity.ok(userProfileService.searchUsers(keyword, page, size));
    }


    @Operation(
            summary = "Admin: Update account status",
            description = "Admin operation to update any user's account status (ACTIVE, PASSIVE, SUSPENDED, DELETED)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @PatchMapping("/admin/{userId}/status")
    public ResponseEntity<DataResponseMessage<UserProfileResponse>> updateAccountStatus(
            @Parameter(description = "Profile ID of the target user", required = true)
            @PathVariable Long userId,
            @Valid @RequestBody UpdateAccountStatusRequest request) {
        log.info("PATCH /v1/api/users/admin/{}/status - newStatus: {}", userId, request.getAccountStatus());
        return ResponseEntity.ok(userProfileService.updateAccountStatus(userId, request));
    }

    @Operation(
            summary = "Admin: Soft delete user profile",
            description = "Admin operation to soft-delete a user profile. " +
                          "Sets accountStatus to DELETED and profileVisibility to PRIVATE. " +
                          "The user will no longer appear in any search or public views."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @DeleteMapping("/admin/{userId}")
    public ResponseEntity<ResponseMessage> deleteProfile(
            @Parameter(description = "Profile ID of the target user", required = true)
            @PathVariable Long userId) {
        log.info("DELETE /v1/api/users/admin/{}", userId);
        return ResponseEntity.ok(userProfileService.deleteProfile(userId));
    }


    @Operation(
            summary = "Internal: Get user by auth ID",
            description = "Internal endpoint for service-to-service communication. " +
                          "Returns minimal user information needed by post-service, friend-service, chat-service, etc. " +
                          "TODO: Restrict to internal network / service-to-service auth in production."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Internal user info retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/internal/auth/{authUserId}")
    public ResponseEntity<DataResponseMessage<InternalUserResponse>> getInternalUserByAuthUserId(
            @Parameter(description = "Auth service user ID", required = true)
            @PathVariable Long authUserId) {
        log.debug("[INTERNAL] GET /v1/api/users/internal/auth/{}", authUserId);
        return ResponseEntity.ok(userProfileService.getInternalUserByAuthUserId(authUserId));
    }

    @Operation(
            summary = "Internal: Get user by username",
            description = "Internal endpoint for service-to-service communication. " +
                          "Returns minimal user information by username. " +
                          "TODO: Restrict to internal network / service-to-service auth in production."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Internal user info retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/internal/username/{username}")
    public ResponseEntity<DataResponseMessage<InternalUserResponse>> getInternalUserByUsername(
            @Parameter(description = "Username of the target user", required = true)
            @PathVariable String username) {
        log.debug("[INTERNAL] GET /v1/api/users/internal/username/{}", username);
        return ResponseEntity.ok(userProfileService.getInternalUserByUsername(username));
    }
}
