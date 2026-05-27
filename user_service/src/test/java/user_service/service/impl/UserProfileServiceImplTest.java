package user_service.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import user_service.common.response.DataResponseMessage;
import user_service.dto.request.CreateUserProfileRequest;
import user_service.dto.request.UpdateAccountStatusRequest;
import user_service.dto.request.UpdateProfileImageRequest;
import user_service.dto.request.UpdateUserProfileRequest;
import user_service.dto.response.InternalUserResponse;
import user_service.dto.response.ProfileCompletionResponse;
import user_service.dto.response.PublicUserProfileResponse;
import user_service.dto.response.UserProfileResponse;
import user_service.dto.response.UserSummaryResponse;
import user_service.entity.UserProfile;
import user_service.enums.AccountStatus;
import user_service.enums.ProfileVisibility;
import user_service.exception.DuplicateUsernameException;
import user_service.exception.InactiveUserException;
import user_service.exception.InvalidProfileDataException;
import user_service.exception.PrivateProfileException;
import user_service.exception.UserProfileAlreadyExistsException;
import user_service.exception.UserProfileNotFoundException;
import user_service.mapper.UserProfileMapper;
import user_service.repository.UserProfileRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

	@Mock
	private UserProfileRepository userProfileRepository;

	@Mock
	private UserProfileMapper userProfileMapper;

	@InjectMocks
	private UserProfileServiceImpl userProfileService;

	@Test
	void createProfile_ShouldCreateProfile_WhenRequestIsValid() {
		CreateUserProfileRequest request = CreateUserProfileRequest.builder()
				.authUserId(10L)
				.username("john_doe")
				.firstName("John")
				.lastName("Doe")
				.phoneNumber("05551234567")
				.build();

		when(userProfileRepository.existsByAuthUserId(10L)).thenReturn(false);
		when(userProfileRepository.existsByUsername("john_doe")).thenReturn(false);
		when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));
		when(userProfileMapper.toUserProfileResponse(any(UserProfile.class))).thenReturn(UserProfileResponse.builder().id(1L).build());

		DataResponseMessage<UserProfileResponse> response = userProfileService.createProfile(request);

		assertThat(response.getData().getId()).isEqualTo(1L);

		ArgumentCaptor<UserProfile> savedCaptor = ArgumentCaptor.forClass(UserProfile.class);
		verify(userProfileRepository, org.mockito.Mockito.times(2)).save(savedCaptor.capture());
		assertThat(savedCaptor.getValue().getAuthUserId()).isEqualTo(10L);
	}

	@Test
	void createProfile_ShouldThrow_WhenAuthUserIdAlreadyExists() {
		CreateUserProfileRequest request = CreateUserProfileRequest.builder()
				.authUserId(10L)
				.username("john_doe")
				.firstName("John")
				.lastName("Doe")
				.build();

		when(userProfileRepository.existsByAuthUserId(10L)).thenReturn(true);

		assertThatThrownBy(() -> userProfileService.createProfile(request))
				.isInstanceOf(UserProfileAlreadyExistsException.class);

		verify(userProfileRepository, never()).save(any(UserProfile.class));
	}

	@Test
	void createProfile_ShouldThrow_WhenUsernameAlreadyExists() {
		CreateUserProfileRequest request = CreateUserProfileRequest.builder()
				.authUserId(10L)
				.username("john_doe")
				.firstName("John")
				.lastName("Doe")
				.build();

		when(userProfileRepository.existsByAuthUserId(10L)).thenReturn(false);
		when(userProfileRepository.existsByUsername("john_doe")).thenReturn(true);

		assertThatThrownBy(() -> userProfileService.createProfile(request))
				.isInstanceOf(DuplicateUsernameException.class);

		verify(userProfileRepository, never()).save(any(UserProfile.class));
	}

	@Test
	void createProfile_ShouldThrowNullPointerException_WhenRequestIsNull() {
		assertThatThrownBy(() -> userProfileService.createProfile(null))
				.isInstanceOf(NullPointerException.class);
	}

	@Test
	void getMyProfile_ShouldReturnProfile_WhenAuthUserIdExists() {
		when(userProfileRepository.findByAuthUserId(10L)).thenReturn(Optional.of(baseProfile()));
		when(userProfileMapper.toUserProfileResponse(any(UserProfile.class)))
				.thenReturn(UserProfileResponse.builder().id(1L).build());

		DataResponseMessage<UserProfileResponse> response = userProfileService.getMyProfile(10L);

		assertThat(response.getData().getId()).isEqualTo(1L);
	}

	@Test
	void getMyProfile_ShouldThrow_WhenAuthUserIdNotFound() {
		when(userProfileRepository.findByAuthUserId(10L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userProfileService.getMyProfile(10L))
				.isInstanceOf(UserProfileNotFoundException.class);
	}

	@Test
	void getProfileById_ShouldReturnProfile_WhenIdExists() {
		when(userProfileRepository.findById(1L)).thenReturn(Optional.of(baseProfile()));
		when(userProfileMapper.toUserProfileResponse(any(UserProfile.class)))
				.thenReturn(UserProfileResponse.builder().id(1L).build());

		DataResponseMessage<UserProfileResponse> response = userProfileService.getProfileById(1L);

		assertThat(response.getData().getId()).isEqualTo(1L);
	}

	@Test
	void getProfileById_ShouldThrow_WhenIdNotFound() {
		when(userProfileRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userProfileService.getProfileById(1L))
				.isInstanceOf(UserProfileNotFoundException.class);
	}

	@Test
	void getProfileByAuthUserId_ShouldReturnProfile_WhenAuthUserIdExists() {
		when(userProfileRepository.findByAuthUserId(10L)).thenReturn(Optional.of(baseProfile()));
		when(userProfileMapper.toUserProfileResponse(any(UserProfile.class)))
				.thenReturn(UserProfileResponse.builder().id(1L).build());

		DataResponseMessage<UserProfileResponse> response = userProfileService.getProfileByAuthUserId(10L);

		assertThat(response.getData().getId()).isEqualTo(1L);
	}

	@Test
	void getPublicProfileByUsername_ShouldReturnPublicProfile_WhenActiveAndPublic() {
		UserProfile profile = baseProfile();
		profile.setAccountStatus(AccountStatus.ACTIVE);
		profile.setProfileVisibility(ProfileVisibility.PUBLIC);

		when(userProfileRepository.findByUsername("john_doe")).thenReturn(Optional.of(profile));
		when(userProfileMapper.toPublicUserProfileResponse(profile))
				.thenReturn(PublicUserProfileResponse.builder().id(1L).build());

		DataResponseMessage<PublicUserProfileResponse> response =
				userProfileService.getPublicProfileByUsername("john_doe");

		assertThat(response.getData().getId()).isEqualTo(1L);
	}

	@Test
	void getPublicProfileByUsername_ShouldThrow_WhenUserNotFound() {
		when(userProfileRepository.findByUsername("missing")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userProfileService.getPublicProfileByUsername("missing"))
				.isInstanceOf(UserProfileNotFoundException.class);
	}

	@Test
	void getPublicProfileByUsername_ShouldThrow_WhenUserIsInactive() {
		UserProfile profile = baseProfile();
		profile.setAccountStatus(AccountStatus.PASSIVE);

		when(userProfileRepository.findByUsername("john_doe")).thenReturn(Optional.of(profile));

		assertThatThrownBy(() -> userProfileService.getPublicProfileByUsername("john_doe"))
				.isInstanceOf(InactiveUserException.class);
	}

	@Test
	void getPublicProfileByUsername_ShouldThrow_WhenProfileIsPrivate() {
		UserProfile profile = baseProfile();
		profile.setAccountStatus(AccountStatus.ACTIVE);
		profile.setProfileVisibility(ProfileVisibility.PRIVATE);

		when(userProfileRepository.findByUsername("john_doe")).thenReturn(Optional.of(profile));

		assertThatThrownBy(() -> userProfileService.getPublicProfileByUsername("john_doe"))
				.isInstanceOf(PrivateProfileException.class);
	}

	@Test
	void updateMyProfile_ShouldUpdateFields_WhenRequestIsValid() {
		UpdateUserProfileRequest request = UpdateUserProfileRequest.builder()
				.firstName("Jane")
				.lastName("Doe")
				.bio("Bio")
				.phoneNumber("05551234567")
				.profileVisibility(ProfileVisibility.PRIVATE)
				.build();

		UserProfile profile = baseProfile();
		profile.setProfileImageUrl("https://img");

		when(userProfileRepository.findByAuthUserId(10L)).thenReturn(Optional.of(profile));
		when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));
		when(userProfileMapper.toUserProfileResponse(any(UserProfile.class)))
				.thenReturn(UserProfileResponse.builder().id(1L).build());

		DataResponseMessage<UserProfileResponse> response = userProfileService.updateMyProfile(10L, request);

		assertThat(response.getData().getId()).isEqualTo(1L);
		assertThat(profile.getFirstName()).isEqualTo("Jane");
		assertThat(profile.getProfileVisibility()).isEqualTo(ProfileVisibility.PRIVATE);
		assertThat(profile.getProfileCompleted()).isTrue();
	}

	@Test
	void updateMyProfile_ShouldThrow_WhenBioExceedsLimit() {
		UpdateUserProfileRequest request = UpdateUserProfileRequest.builder()
				.bio("a".repeat(501))
				.build();

		when(userProfileRepository.findByAuthUserId(10L)).thenReturn(Optional.of(baseProfile()));

		assertThatThrownBy(() -> userProfileService.updateMyProfile(10L, request))
				.isInstanceOf(InvalidProfileDataException.class)
				.hasMessageContaining("Bio cannot exceed");
	}

	@Test
	void updateProfileImage_ShouldUpdateImage_WhenRequestIsValid() {
		UserProfile profile = baseProfile();

		when(userProfileRepository.findByAuthUserId(10L)).thenReturn(Optional.of(profile));
		when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));
		when(userProfileMapper.toUserProfileResponse(any(UserProfile.class)))
				.thenReturn(UserProfileResponse.builder().id(1L).build());

		DataResponseMessage<UserProfileResponse> response = userProfileService.updateProfileImage(
				10L, UpdateProfileImageRequest.builder().profileImageUrl("https://img").build());

		assertThat(response.getData().getId()).isEqualTo(1L);
		assertThat(profile.getProfileImageUrl()).isEqualTo("https://img");
	}

	@Test
	void deactivateMyProfile_ShouldSetStatusPassive_WhenAuthUserIdExists() {
		UserProfile profile = baseProfile();
		when(userProfileRepository.findByAuthUserId(10L)).thenReturn(Optional.of(profile));

		userProfileService.deactivateMyProfile(10L);

		assertThat(profile.getAccountStatus()).isEqualTo(AccountStatus.PASSIVE);
		verify(userProfileRepository).save(profile);
	}

	@Test
	void updateAccountStatus_ShouldUpdateStatus_WhenUserExists() {
		UserProfile profile = baseProfile();
		when(userProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
		when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));
		when(userProfileMapper.toUserProfileResponse(any(UserProfile.class)))
				.thenReturn(UserProfileResponse.builder().id(1L).build());

		DataResponseMessage<UserProfileResponse> response = userProfileService.updateAccountStatus(
				1L, UpdateAccountStatusRequest.builder().accountStatus(AccountStatus.SUSPENDED).build());

		assertThat(response.getData().getId()).isEqualTo(1L);
		assertThat(profile.getAccountStatus()).isEqualTo(AccountStatus.SUSPENDED);
	}

	@Test
	void deleteProfile_ShouldSoftDelete_WhenUserExists() {
		UserProfile profile = baseProfile();
		when(userProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

		userProfileService.deleteProfile(1L);

		assertThat(profile.getAccountStatus()).isEqualTo(AccountStatus.DELETED);
		assertThat(profile.getProfileVisibility()).isEqualTo(ProfileVisibility.PRIVATE);
		verify(userProfileRepository).save(profile);
	}

	@Test
	void searchUsers_ShouldNormalizeKeywordAndReturnSummaries() {
		UserProfile profile = baseProfile();
		Page<UserProfile> page = new PageImpl<>(List.of(profile), PageRequest.of(0, 10), 1);

		when(userProfileRepository.searchUsers(eq("john"), any())).thenReturn(page);
		when(userProfileMapper.toUserSummaryResponse(profile))
				.thenReturn(UserSummaryResponse.builder().id(1L).build());

		DataResponseMessage<?> response = userProfileService.searchUsers(" john ", 0, 10);

		assertThat(response.getData()).isNotNull();
	}

	@Test
	void checkProfileCompletion_ShouldReturnCompletionResponse_WhenUserExists() {
		UserProfile profile = baseProfile();
		when(userProfileRepository.findByAuthUserId(10L)).thenReturn(Optional.of(profile));
		when(userProfileMapper.toProfileCompletionResponse(profile))
				.thenReturn(ProfileCompletionResponse.builder().userId(1L).build());

		DataResponseMessage<ProfileCompletionResponse> response = userProfileService.checkProfileCompletion(10L);

		assertThat(response.getData().getUserId()).isEqualTo(1L);
	}

	@Test
	void getInternalUserByAuthUserId_ShouldReturnInternalUser_WhenExists() {
		UserProfile profile = baseProfile();
		when(userProfileRepository.findByAuthUserId(10L)).thenReturn(Optional.of(profile));
		when(userProfileMapper.toInternalUserResponse(profile))
				.thenReturn(InternalUserResponse.builder().id(1L).build());

		DataResponseMessage<InternalUserResponse> response = userProfileService.getInternalUserByAuthUserId(10L);

		assertThat(response.getData().getId()).isEqualTo(1L);
	}

	@Test
	void getInternalUserByUsername_ShouldThrow_WhenUserNotFound() {
		when(userProfileRepository.findByUsername("missing")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userProfileService.getInternalUserByUsername("missing"))
				.isInstanceOf(UserProfileNotFoundException.class);
	}

	private UserProfile baseProfile() {
		return UserProfile.builder()
				.id(1L)
				.authUserId(10L)
				.username("john_doe")
				.firstName("John")
				.lastName("Doe")
				.profileVisibility(ProfileVisibility.PUBLIC)
				.accountStatus(AccountStatus.ACTIVE)
				.profileCompleted(false)
				.build();
	}
}

