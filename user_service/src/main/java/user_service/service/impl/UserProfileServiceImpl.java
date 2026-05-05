package user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import user_service.common.response.DataResponseMessage;
import user_service.common.response.PageResponse;
import user_service.common.response.ResponseMessage;
import user_service.dto.request.*;
import user_service.dto.response.*;
import user_service.entity.UserProfile;
import user_service.enums.AccountStatus;
import user_service.enums.Department;
import user_service.enums.Faculty;
import user_service.enums.Grade;
import user_service.enums.ProfileVisibility;
import user_service.exception.*;
import user_service.mapper.UserProfileMapper;
import user_service.repository.UserProfileRepository;
import user_service.service.UserProfileService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserProfileService.
 * Contains all business logic for user profile operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    // ============================================================
    // CREATE
    // ============================================================

    @Override
    public DataResponseMessage<UserProfileResponse> createProfile(CreateUserProfileRequest request) {
        log.info("Creating user profile for authUserId: {}", request.getAuthUserId());

        // Check for duplicate authUserId
        if (userProfileRepository.existsByAuthUserId(request.getAuthUserId())) {
            throw new UserProfileAlreadyExistsException(request.getAuthUserId());
        }

        // Check for duplicate username
        if (userProfileRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUsernameException(request.getUsername());
        }

        // Check for duplicate email
        if (userProfileRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        // Check for duplicate studentNumber if provided
        if (request.getStudentNumber() != null && !request.getStudentNumber().isBlank()) {
            if (userProfileRepository.existsByStudentNumber(request.getStudentNumber())) {
                throw new DuplicateStudentNumberException(request.getStudentNumber());
            }
        }

        // Build displayName if not provided
        String displayName = request.getFirstName() + " " + request.getLastName();

        UserProfile userProfile = UserProfile.builder()
                .authUserId(request.getAuthUserId())
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .displayName(displayName)
                .faculty(request.getFaculty())
                .department(request.getDepartment())
                .grade(request.getGrade())
                .studentNumber(request.getStudentNumber())
                .profileVisibility(ProfileVisibility.PUBLIC)
                .accountStatus(AccountStatus.ACTIVE)
                .profileCompleted(false)
                .build();

        UserProfile savedProfile = userProfileRepository.save(userProfile);

        // Calculate and update profile completion
        savedProfile.setProfileCompleted(calculateProfileCompleted(savedProfile));
        userProfileRepository.save(savedProfile);

        log.info("User profile created successfully. Profile ID: {}", savedProfile.getId());
        return DataResponseMessage.success("User profile created successfully.", userProfileMapper.toUserProfileResponse(savedProfile));
    }

    // ============================================================
    // READ
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<UserProfileResponse> getMyProfile(Long authUserId) {
        log.debug("Getting own profile for authUserId: {}", authUserId);
        UserProfile profile = findByAuthUserIdOrThrow(authUserId);
        return DataResponseMessage.success("Profile retrieved successfully.", userProfileMapper.toUserProfileResponse(profile));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<UserProfileResponse> getProfileById(Long id) {
        log.debug("Getting profile by id: {}", id);
        UserProfile profile = findByIdOrThrow(id);
        return DataResponseMessage.success("Profile retrieved successfully.", userProfileMapper.toUserProfileResponse(profile));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<UserProfileResponse> getProfileByAuthUserId(Long authUserId) {
        log.debug("Getting profile by authUserId: {}", authUserId);
        UserProfile profile = findByAuthUserIdOrThrow(authUserId);
        return DataResponseMessage.success("Profile retrieved successfully.", userProfileMapper.toUserProfileResponse(profile));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PublicUserProfileResponse> getPublicProfileByUsername(String username) {
        log.debug("Getting public profile for username: {}", username);

        UserProfile profile = userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new UserProfileNotFoundException("User not found with username: " + username));

        // Ensure user is active
        if (profile.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new InactiveUserException(username);
        }

        // Enforce privacy
        if (profile.getProfileVisibility() == ProfileVisibility.PRIVATE) {
            throw new PrivateProfileException(username);
        }

        return DataResponseMessage.success("Public profile retrieved successfully.", userProfileMapper.toPublicUserProfileResponse(profile));
    }

    // ============================================================
    // UPDATE
    // ============================================================

    @Override
    public DataResponseMessage<UserProfileResponse> updateMyProfile(Long authUserId, UpdateUserProfileRequest request) {
        log.info("Updating profile for authUserId: {}", authUserId);

        UserProfile profile = findByAuthUserIdOrThrow(authUserId);

        // Apply non-null updates (username and email are NOT changed here)
        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName());
        }
        if (request.getDisplayName() != null) {
            profile.setDisplayName(request.getDisplayName());
        }
        if (request.getBio() != null) {
            if (request.getBio().length() > 500) {
                throw new InvalidProfileDataException("Bio cannot exceed 500 characters.");
            }
            profile.setBio(request.getBio());
        }
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getBirthDate() != null) {
            profile.setBirthDate(request.getBirthDate());
        }
        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }
        if (request.getFaculty() != null) {
            profile.setFaculty(request.getFaculty());
        }
        if (request.getDepartment() != null) {
            profile.setDepartment(request.getDepartment());
        }
        if (request.getGrade() != null) {
            profile.setGrade(request.getGrade());
        }
        if (request.getStudentNumber() != null) {
            // If student number is changing, check uniqueness against others
            if (!request.getStudentNumber().equals(profile.getStudentNumber())) {
                if (userProfileRepository.existsByStudentNumberAndIdNot(request.getStudentNumber(), profile.getId())) {
                    throw new DuplicateStudentNumberException(request.getStudentNumber());
                }
            }
            profile.setStudentNumber(request.getStudentNumber());
        }
        if (request.getLocation() != null) {
            profile.setLocation(request.getLocation());
        }
        if (request.getWebsiteUrl() != null) {
            profile.setWebsiteUrl(request.getWebsiteUrl());
        }
        if (request.getInstagramUrl() != null) {
            profile.setInstagramUrl(request.getInstagramUrl());
        }
        if (request.getLinkedinUrl() != null) {
            profile.setLinkedinUrl(request.getLinkedinUrl());
        }
        if (request.getGithubUrl() != null) {
            profile.setGithubUrl(request.getGithubUrl());
        }
        if (request.getProfileVisibility() != null) {
            profile.setProfileVisibility(request.getProfileVisibility());
        }

        // Recalculate profile completion
        profile.setProfileCompleted(calculateProfileCompleted(profile));

        UserProfile updatedProfile = userProfileRepository.save(profile);
        log.info("Profile updated successfully for authUserId: {}", authUserId);
        return DataResponseMessage.success("Profile updated successfully.", userProfileMapper.toUserProfileResponse(updatedProfile));
    }

    @Override
    public DataResponseMessage<UserProfileResponse> updateProfileImage(Long authUserId, UpdateProfileImageRequest request) {
        log.info("Updating profile image for authUserId: {}", authUserId);
        UserProfile profile = findByAuthUserIdOrThrow(authUserId);
        profile.setProfileImageUrl(request.getProfileImageUrl());
        profile.setProfileCompleted(calculateProfileCompleted(profile));
        UserProfile saved = userProfileRepository.save(profile);
        return DataResponseMessage.success("Profile image updated successfully.", userProfileMapper.toUserProfileResponse(saved));
    }

    @Override
    public DataResponseMessage<UserProfileResponse> updateCoverImage(Long authUserId, UpdateCoverImageRequest request) {
        log.info("Updating cover image for authUserId: {}", authUserId);
        UserProfile profile = findByAuthUserIdOrThrow(authUserId);
        profile.setCoverImageUrl(request.getCoverImageUrl());
        UserProfile saved = userProfileRepository.save(profile);
        return DataResponseMessage.success("Cover image updated successfully.", userProfileMapper.toUserProfileResponse(saved));
    }

    // ============================================================
    // STATUS MANAGEMENT
    // ============================================================

    @Override
    public ResponseMessage deactivateMyProfile(Long authUserId) {
        log.info("Deactivating profile for authUserId: {}", authUserId);
        UserProfile profile = findByAuthUserIdOrThrow(authUserId);
        profile.setAccountStatus(AccountStatus.PASSIVE);
        userProfileRepository.save(profile);
        return ResponseMessage.success("Your account has been deactivated successfully.");
    }

    @Override
    public DataResponseMessage<UserProfileResponse> updateAccountStatus(Long userId, UpdateAccountStatusRequest request) {
        log.info("Admin updating account status for userId: {} to {}", userId, request.getAccountStatus());
        UserProfile profile = findByIdOrThrow(userId);
        profile.setAccountStatus(request.getAccountStatus());
        UserProfile saved = userProfileRepository.save(profile);
        return DataResponseMessage.success("Account status updated successfully.", userProfileMapper.toUserProfileResponse(saved));
    }

    @Override
    public ResponseMessage deleteProfile(Long userId) {
        log.info("Soft deleting profile for userId: {}", userId);
        UserProfile profile = findByIdOrThrow(userId);

        // Soft delete: mark as DELETED and make profile PRIVATE
        profile.setAccountStatus(AccountStatus.DELETED);
        profile.setProfileVisibility(ProfileVisibility.PRIVATE);

        userProfileRepository.save(profile);
        return ResponseMessage.success("User profile deleted successfully.");
    }

    // ============================================================
    // SEARCH
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<UserSummaryResponse>> searchUsers(
            String keyword,
            Faculty faculty,
            Department department,
            Grade grade,
            int page,
            int size) {

        log.debug("Searching users - keyword: {}, faculty: {}, department: {}, grade: {}", keyword, faculty, department, grade);

        Pageable pageable = PageRequest.of(page, size);

        // Normalize empty keyword to null for JPQL query
        String normalizedKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        Page<UserProfile> resultPage = userProfileRepository.searchUsers(normalizedKeyword, faculty, department, grade, pageable);

        List<UserSummaryResponse> summaries = resultPage.getContent()
                .stream()
                .map(userProfileMapper::toUserSummaryResponse)
                .collect(Collectors.toList());

        PageResponse<UserSummaryResponse> pageResponse = PageResponse.from(resultPage, summaries);
        return DataResponseMessage.success("Users retrieved successfully.", pageResponse);
    }

    // ============================================================
    // PROFILE COMPLETION
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<ProfileCompletionResponse> checkProfileCompletion(Long authUserId) {
        log.debug("Checking profile completion for authUserId: {}", authUserId);
        UserProfile profile = findByAuthUserIdOrThrow(authUserId);
        ProfileCompletionResponse response = userProfileMapper.toProfileCompletionResponse(profile);
        return DataResponseMessage.success("Profile completion status retrieved.", response);
    }

    // ============================================================
    // INTERNAL
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<InternalUserResponse> getInternalUserByAuthUserId(Long authUserId) {
        log.debug("[INTERNAL] Getting user info by authUserId: {}", authUserId);
        UserProfile profile = findByAuthUserIdOrThrow(authUserId);
        return DataResponseMessage.success("Internal user info retrieved.", userProfileMapper.toInternalUserResponse(profile));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<InternalUserResponse> getInternalUserByUsername(String username) {
        log.debug("[INTERNAL] Getting user info by username: {}", username);
        UserProfile profile = userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new UserProfileNotFoundException("User not found with username: " + username));
        return DataResponseMessage.success("Internal user info retrieved.", userProfileMapper.toInternalUserResponse(profile));
    }

    // ============================================================
    // PRIVATE HELPERS
    // ============================================================

    private UserProfile findByIdOrThrow(Long id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException(id));
    }

    private UserProfile findByAuthUserIdOrThrow(Long authUserId) {
        return userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for authUserId: " + authUserId));
    }

    /**
     * Returns true if the core required fields are filled.
     */
    private boolean calculateProfileCompleted(UserProfile profile) {
        return profile.getFirstName() != null && !profile.getFirstName().isBlank()
                && profile.getLastName() != null && !profile.getLastName().isBlank()
                && profile.getEmail() != null && !profile.getEmail().isBlank()
                && profile.getUsername() != null && !profile.getUsername().isBlank()
                && profile.getFaculty() != null
                && profile.getDepartment() != null
                && profile.getGrade() != null
                && profile.getProfileImageUrl() != null && !profile.getProfileImageUrl().isBlank();
    }
}
