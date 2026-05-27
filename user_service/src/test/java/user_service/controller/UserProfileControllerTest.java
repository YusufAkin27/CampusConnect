package user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import user_service.common.response.DataResponseMessage;
import user_service.common.response.PageResponse;
import user_service.common.response.ResponseMessage;
import user_service.dto.request.CreateUserProfileRequest;
import user_service.dto.request.UpdateAccountStatusRequest;
import user_service.dto.request.UpdateProfileImageRequest;
import user_service.dto.request.UpdateUserProfileRequest;
import user_service.dto.response.PublicUserProfileResponse;
import user_service.dto.response.UserProfileResponse;
import user_service.dto.response.UserSummaryResponse;
import user_service.exception.DuplicateUsernameException;
import user_service.exception.InvalidProfileDataException;
import user_service.exception.PrivateProfileException;
import user_service.exception.UnauthorizedUserOperationException;
import user_service.exception.UserProfileNotFoundException;
import user_service.exception.handler.GlobalExceptionHandler;
import user_service.service.UserProfileService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserProfileService userProfileService;

    @Test
    void createProfile_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        CreateUserProfileRequest request = CreateUserProfileRequest.builder()
                .authUserId(10L)
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("05551234567")
                .build();

        when(userProfileService.createProfile(any(CreateUserProfileRequest.class)))
                .thenReturn(DataResponseMessage.success("ok", UserProfileResponse.builder().id(1L).build()));

        mockMvc.perform(post("/v1/api/users/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void createProfile_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        CreateUserProfileRequest request = CreateUserProfileRequest.builder()
                .authUserId(null)
                .username("ab")
                .firstName("")
                .lastName("")
                .phoneNumber("123")
                .build();

        mockMvc.perform(post("/v1/api/users/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userProfileService, never()).createProfile(any(CreateUserProfileRequest.class));
    }

    @Test
    void getMyProfile_ShouldReturnInternalServerError_WhenHeaderMissing() throws Exception {
        mockMvc.perform(get("/v1/api/users/me"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"));
    }

    @Test
    void getMyProfile_ShouldReturnOk_WhenHeaderPresent() throws Exception {
        when(userProfileService.getMyProfile(10L))
                .thenReturn(DataResponseMessage.success("ok", UserProfileResponse.builder().id(1L).build()));

        mockMvc.perform(get("/v1/api/users/me")
                        .header("X-Auth-User-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void updateMyProfile_ShouldReturnBadRequest_WhenRequestInvalid() throws Exception {
        UpdateUserProfileRequest request = UpdateUserProfileRequest.builder()
                .bio("a".repeat(501))
                .build();

        mockMvc.perform(put("/v1/api/users/me")
                        .header("X-Auth-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProfileImage_ShouldReturnBadRequest_WhenUrlMissing() throws Exception {
        UpdateProfileImageRequest request = UpdateProfileImageRequest.builder().profileImageUrl(" ").build();

        mockMvc.perform(patch("/v1/api/users/me/profile-image")
                        .header("X-Auth-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deactivateMyProfile_ShouldReturnOk_WhenHeaderPresent() throws Exception {
        when(userProfileService.deactivateMyProfile(10L))
                .thenReturn(ResponseMessage.success("ok"));

        mockMvc.perform(patch("/v1/api/users/me/deactivate")
                        .header("X-Auth-User-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("ok"));
    }

    @Test
    void getPublicProfile_ShouldReturnOk_WhenServiceReturnsData() throws Exception {
        when(userProfileService.getPublicProfileByUsername("john"))
                .thenReturn(DataResponseMessage.success("ok", PublicUserProfileResponse.builder().id(1L).build()));

        mockMvc.perform(get("/v1/api/users/username/john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void searchUsers_ShouldReturnOk_WhenParamsValid() throws Exception {
        PageResponse<UserSummaryResponse> page = PageResponse.<UserSummaryResponse>builder()
                .content(List.of(UserSummaryResponse.builder().id(1L).build()))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();
        when(userProfileService.searchUsers(eq("john"), eq(0), eq(10)))
                .thenReturn(DataResponseMessage.success("ok", page));

        mockMvc.perform(get("/v1/api/users/search")
                        .param("keyword", "john")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1L));
    }

    @Test
    void updateAccountStatus_ShouldReturnBadRequest_WhenStatusMissing() throws Exception {
        UpdateAccountStatusRequest request = UpdateAccountStatusRequest.builder().accountStatus(null).build();

        mockMvc.perform(patch("/v1/api/users/admin/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProfile_ShouldReturnOk_WhenServiceReturnsSuccess() throws Exception {
        when(userProfileService.deleteProfile(1L))
                .thenReturn(ResponseMessage.success("deleted"));

        mockMvc.perform(delete("/v1/api/users/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("deleted"));
    }

    @Test
    void createProfile_ShouldReturnConflict_WhenDuplicateUsername() throws Exception {
        CreateUserProfileRequest request = CreateUserProfileRequest.builder()
                .authUserId(10L)
                .username("john_doe")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("05551234567")
                .build();

        when(userProfileService.createProfile(any(CreateUserProfileRequest.class)))
                .thenThrow(new DuplicateUsernameException("john_doe"));

        mockMvc.perform(post("/v1/api/users/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateMyProfile_ShouldReturnBadRequest_WhenServiceThrowsInvalidProfileData() throws Exception {
        UpdateUserProfileRequest request = UpdateUserProfileRequest.builder().bio("Bio").build();
        when(userProfileService.updateMyProfile(eq(10L), any(UpdateUserProfileRequest.class)))
                .thenThrow(new InvalidProfileDataException("bad"));

        mockMvc.perform(put("/v1/api/users/me")
                        .header("X-Auth-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPublicProfile_ShouldReturnForbidden_WhenProfilePrivate() throws Exception {
        when(userProfileService.getPublicProfileByUsername("john"))
                .thenThrow(new PrivateProfileException("john"));

        mockMvc.perform(get("/v1/api/users/username/john"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProfileById_ShouldReturnNotFound_WhenServiceThrowsNotFound() throws Exception {
        when(userProfileService.getProfileById(1L))
                .thenThrow(new UserProfileNotFoundException(1L));

        mockMvc.perform(get("/v1/api/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMyProfile_ShouldReturnForbidden_WhenUnauthorizedOperation() throws Exception {
        when(userProfileService.getMyProfile(10L))
                .thenThrow(new UnauthorizedUserOperationException("denied"));

        mockMvc.perform(get("/v1/api/users/me")
                        .header("X-Auth-User-Id", "10"))
                .andExpect(status().isForbidden());
    }
}

