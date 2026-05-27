package user_service.mapper;

import org.junit.jupiter.api.Test;
import user_service.dto.response.ProfileCompletionResponse;
import user_service.dto.response.UserProfileResponse;
import user_service.entity.UserProfile;
import user_service.enums.AccountStatus;
import user_service.enums.ProfileVisibility;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfileMapperTest {

    private final UserProfileMapper mapper = new UserProfileMapper();

    @Test
    void toUserProfileResponse_ShouldMapAllFields_WhenUserIsPresent() {
        UserProfile profile = UserProfile.builder()
                .id(1L)
                .authUserId(10L)
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .bio("Bio")
                .profileImageUrl("https://img")
                .phoneNumber("05551234567")
                .profileVisibility(ProfileVisibility.PUBLIC)
                .accountStatus(AccountStatus.ACTIVE)
                .profileCompleted(true)
                .build();

        UserProfileResponse response = mapper.toUserProfileResponse(profile);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getAuthUserId()).isEqualTo(10L);
        assertThat(response.getUsername()).isEqualTo("john_doe");
        assertThat(response.getProfileVisibility()).isEqualTo(ProfileVisibility.PUBLIC);
        assertThat(response.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    void toProfileCompletionResponse_ShouldListMissingFields_WhenProfileIncomplete() {
        UserProfile profile = UserProfile.builder()
                .id(1L)
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .profileImageUrl("")
                .bio(null)
                .phoneNumber(null)
                .build();

        ProfileCompletionResponse response = mapper.toProfileCompletionResponse(profile);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getMissingFields()).contains("profileImageUrl", "bio", "phoneNumber");
        assertThat(response.getCompleted()).isFalse();
        assertThat(response.getCompletionRate()).isLessThan(100);
    }

    @Test
    void toUserProfileResponse_ShouldReturnNull_WhenUserIsNull() {
        assertThat(mapper.toUserProfileResponse(null)).isNull();
    }
}

