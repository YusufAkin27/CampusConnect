package user_service.service;

import user_service.common.response.DataResponseMessage;
import user_service.common.response.PageResponse;
import user_service.common.response.ResponseMessage;
import user_service.dto.request.*;
import user_service.dto.response.*;
import user_service.enums.Department;
import user_service.enums.Faculty;
import user_service.enums.Grade;

/**
 * Service interface for user profile operations.
 * Defines all business operations available for user profile management.
 */
public interface UserProfileService {

    /**
     * Creates a new user profile.
     * Typically called by auth-service after successful user registration.
     *
     * @param request profile creation data
     * @return created user profile response
     */
    DataResponseMessage<UserProfileResponse> createProfile(CreateUserProfileRequest request);

    /**
     * Returns the profile of the currently authenticated user.
     *
     * @param authUserId ID from auth-service
     * @return full profile response
     */
    DataResponseMessage<UserProfileResponse> getMyProfile(Long authUserId);

    /**
     * Returns a user profile by its internal profile ID.
     *
     * @param id profile ID
     * @return full profile response
     */
    DataResponseMessage<UserProfileResponse> getProfileById(Long id);

    /**
     * Returns a user profile by auth-service user ID.
     *
     * @param authUserId auth-service user ID
     * @return full profile response
     */
    DataResponseMessage<UserProfileResponse> getProfileByAuthUserId(Long authUserId);

    /**
     * Returns the public profile view for a given username.
     * Throws PrivateProfileException if profile is PRIVATE.
     *
     * @param username the username
     * @return public profile response
     */
    DataResponseMessage<PublicUserProfileResponse> getPublicProfileByUsername(String username);

    /**
     * Updates the authenticated user's profile.
     * Only non-null fields in the request will be updated.
     *
     * @param authUserId  ID from auth-service
     * @param request update request with optional fields
     * @return updated full profile response
     */
    DataResponseMessage<UserProfileResponse> updateMyProfile(Long authUserId, UpdateUserProfileRequest request);

    /**
     * Updates the authenticated user's profile image URL.
     *
     * @param authUserId  ID from auth-service
     * @param request request containing the new profile image URL
     * @return updated profile response
     */
    DataResponseMessage<UserProfileResponse> updateProfileImage(Long authUserId, UpdateProfileImageRequest request);

    /**
     * Updates the authenticated user's cover image URL.
     *
     * @param authUserId  ID from auth-service
     * @param request request containing the new cover image URL
     * @return updated profile response
     */
    DataResponseMessage<UserProfileResponse> updateCoverImage(Long authUserId, UpdateCoverImageRequest request);

    /**
     * Deactivates (sets to PASSIVE) the authenticated user's own account.
     *
     * @param authUserId  ID from auth-service
     * @return success message
     */
    ResponseMessage deactivateMyProfile(Long authUserId);

    /**
     * Admin operation: updates any user's account status.
     *
     * @param userId  profile ID
     * @param request new account status
     * @return updated profile response
     */
    DataResponseMessage<UserProfileResponse> updateAccountStatus(Long userId, UpdateAccountStatusRequest request);

    /**
     * Searches active users by keyword and optional student filters with pagination.
     *
     * @param keyword    search keyword (username, name, displayName, email)
     * @param faculty    optional faculty filter
     * @param department optional department filter
     * @param grade      optional grade filter
     * @param page       page number (0-indexed)
     * @param size       page size
     * @return paginated list of user summaries
     */
    DataResponseMessage<PageResponse<UserSummaryResponse>> searchUsers(
            String keyword,
            Faculty faculty,
            Department department,
            Grade grade,
            int page,
            int size
    );

    /**
     * Checks profile completion status and returns percentage with missing fields.
     *
     * @param authUserId  ID from auth-service
     * @return profile completion response
     */
    DataResponseMessage<ProfileCompletionResponse> checkProfileCompletion(Long authUserId);

    /**
     * Internal: returns minimal user info by authUserId for other services.
     *
     * @param authUserId auth-service user ID
     * @return internal user response
     */
    DataResponseMessage<InternalUserResponse> getInternalUserByAuthUserId(Long authUserId);

    /**
     * Internal: returns minimal user info by username for other services.
     *
     * @param username username
     * @return internal user response
     */
    DataResponseMessage<InternalUserResponse> getInternalUserByUsername(String username);

    /**
     * Admin operation: soft deletes a user profile.
     * Sets accountStatus to DELETED and profileVisibility to PRIVATE.
     *
     * @param userId profile ID
     * @return success message
     */
    ResponseMessage deleteProfile(Long userId);
}
