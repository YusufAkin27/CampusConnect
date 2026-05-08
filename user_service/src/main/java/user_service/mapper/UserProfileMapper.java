package user_service.mapper;

import org.springframework.stereotype.Component;
import user_service.dto.response.*;
import user_service.entity.UserProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Manual mapper for converting UserProfile entity to various response DTOs.
 * MapStruct is intentionally not used to keep control over null safety and custom logic.
 */
@Component
public class UserProfileMapper {

    /**
     * Converts UserProfile to full UserProfileResponse (private view).
     */
    public UserProfileResponse toUserProfileResponse(UserProfile user) {
        if (user == null) return null;

        return UserProfileResponse.builder()
                .id(user.getId())
                .authUserId(user.getAuthUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .displayName(user.getDisplayName())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .coverImageUrl(user.getCoverImageUrl())
                .phoneNumber(user.getPhoneNumber())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .faculty(user.getFaculty())
                .department(user.getDepartment())
                .grade(user.getGrade())
                .studentNumber(user.getStudentNumber())
                .location(user.getLocation())
                .websiteUrl(user.getWebsiteUrl())
                .instagramUrl(user.getInstagramUrl())
                .linkedinUrl(user.getLinkedinUrl())
                .githubUrl(user.getGithubUrl())
                .profileVisibility(user.getProfileVisibility())
                .accountStatus(user.getAccountStatus())
                .profileCompleted(user.getProfileCompleted())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


    public PublicUserProfileResponse toPublicUserProfileResponse(UserProfile user) {
        if (user == null) return null;

        return PublicUserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .displayName(user.getDisplayName())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .coverImageUrl(user.getCoverImageUrl())
                .faculty(user.getFaculty())
                .department(user.getDepartment())
                .grade(user.getGrade())
                .location(user.getLocation())
                .instagramUrl(user.getInstagramUrl())
                .linkedinUrl(user.getLinkedinUrl())
                .githubUrl(user.getGithubUrl())
                .build();
    }


    public UserSummaryResponse toUserSummaryResponse(UserProfile user) {
        if (user == null) return null;

        return UserSummaryResponse.builder()
                .id(user.getId())
                .authUserId(user.getAuthUserId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .profileImageUrl(user.getProfileImageUrl())
                .faculty(user.getFaculty())
                .department(user.getDepartment())
                .grade(user.getGrade())
                .build();
    }


    public InternalUserResponse toInternalUserResponse(UserProfile user) {
        if (user == null) return null;

        return InternalUserResponse.builder()
                .id(user.getId())
                .authUserId(user.getAuthUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .profileImageUrl(user.getProfileImageUrl())
                .accountStatus(user.getAccountStatus())
                .build();
    }


    public ProfileCompletionResponse toProfileCompletionResponse(UserProfile user) {
        if (user == null) return null;

        List<String> missingFields = new ArrayList<>();

        if (isBlank(user.getFirstName()))       missingFields.add("firstName");
        if (isBlank(user.getLastName()))         missingFields.add("lastName");
        if (isBlank(user.getUsername()))         missingFields.add("username");
        if (isBlank(user.getEmail()))            missingFields.add("email");
        if (user.getFaculty() == null)           missingFields.add("faculty");
        if (user.getDepartment() == null)        missingFields.add("department");
        if (user.getGrade() == null)             missingFields.add("grade");
        if (isBlank(user.getProfileImageUrl()))  missingFields.add("profileImageUrl");
        if (isBlank(user.getBio()))              missingFields.add("bio");
        if (user.getGender() == null)            missingFields.add("gender");
        if (user.getBirthDate() == null)         missingFields.add("birthDate");
        if (isBlank(user.getPhoneNumber()))      missingFields.add("phoneNumber");

        int totalFields = 12;
        int completedFields = totalFields - missingFields.size();
        int completionRate = (int) Math.round((completedFields * 100.0) / totalFields);
        boolean completed = missingFields.isEmpty();

        return ProfileCompletionResponse.builder()
                .userId(user.getId())
                .completed(completed)
                .completionRate(completionRate)
                .missingFields(missingFields)
                .build();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
