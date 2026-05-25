package user_service.service;

import user_service.common.response.DataResponseMessage;
import user_service.common.response.PageResponse;
import user_service.common.response.ResponseMessage;
import user_service.dto.request.*;
import user_service.dto.response.*;
import user_service.enums.Department;
import user_service.enums.Faculty;
import user_service.enums.Grade;


public interface UserProfileService {


    DataResponseMessage<UserProfileResponse> createProfile(CreateUserProfileRequest request);


    DataResponseMessage<UserProfileResponse> getMyProfile(Long authUserId);


    DataResponseMessage<UserProfileResponse> getProfileById(Long id);

    DataResponseMessage<UserProfileResponse> getProfileByAuthUserId(Long authUserId);


    DataResponseMessage<PublicUserProfileResponse> getPublicProfileByUsername(String username);


    DataResponseMessage<UserProfileResponse> updateMyProfile(Long authUserId, UpdateUserProfileRequest request);


    DataResponseMessage<UserProfileResponse> updateProfileImage(Long authUserId, UpdateProfileImageRequest request);



    ResponseMessage deactivateMyProfile(Long authUserId);


    DataResponseMessage<UserProfileResponse> updateAccountStatus(Long userId, UpdateAccountStatusRequest request);


    DataResponseMessage<PageResponse<UserSummaryResponse>> searchUsers(
            String keyword,
            Faculty faculty,
            Department department,
            Grade grade,
            int page,
            int size
    );


    DataResponseMessage<ProfileCompletionResponse> checkProfileCompletion(Long authUserId);


    DataResponseMessage<InternalUserResponse> getInternalUserByAuthUserId(Long authUserId);


    DataResponseMessage<InternalUserResponse> getInternalUserByUsername(String username);


    ResponseMessage deleteProfile(Long userId);
}
