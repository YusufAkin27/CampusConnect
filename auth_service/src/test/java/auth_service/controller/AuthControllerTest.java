package auth_service.controller;

import auth_service.config.JwtAuthenticationFilter;
import auth_service.dto.request.ChangePasswordRequest;
import auth_service.dto.request.LoginRequest;
import auth_service.dto.request.LogoutRequest;
import auth_service.dto.request.RefreshTokenRequest;
import auth_service.dto.request.RegisterRequest;
import auth_service.dto.response.ApiResponse;
import auth_service.dto.response.AuthResponse;
import auth_service.dto.response.TokenResponse;
import auth_service.dto.response.UserAuthResponse;
import auth_service.entity.Role;
import auth_service.exception.GlobalExceptionHandler;
import auth_service.exception.InvalidPasswordException;
import auth_service.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void register_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("Password123")
                .build();

        AuthResponse response = AuthResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .expiresIn(900_000L)
                .user(UserAuthResponse.builder().id(1L).username("john_doe").email("john@example.com").role(Role.USER).build())
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("ab")
                .email("not-email")
                .password("weak")
                .build();

        mockMvc.perform(post("/v1/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenServiceThrowsInvalidPasswordException() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail("john_doe")
                .password("Password123")
                .build();

        when(authService.login(any(LoginRequest.class))).thenThrow(new InvalidPasswordException());

        mockMvc.perform(post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void refreshToken_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        RefreshTokenRequest request = RefreshTokenRequest.builder().refreshToken("refresh-token").build();
        TokenResponse response = TokenResponse.builder()
                .accessToken("new-access")
                .refreshToken("new-refresh")
                .expiresIn(900_000L)
                .build();

        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"));
    }

    @Test
    void validateToken_ShouldReturnUnauthorized_WhenTokenIsMissing() throws Exception {
        mockMvc.perform(post("/v1/api/auth/validate-token"))
                .andExpect(status().isUnauthorized());

        verify(authService, never()).validateToken(any());
    }

    @Test
    void validateToken_ShouldReturnOk_WhenTokenProvidedAsRequestParam() throws Exception {
        UserAuthResponse response = UserAuthResponse.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .role(Role.USER)
                .build();

        when(authService.validateToken("token-value")).thenReturn(response);

        mockMvc.perform(post("/v1/api/auth/validate-token").param("token", "token-value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    void me_ShouldReturnOk_WhenHeaderExists() throws Exception {
        UserAuthResponse response = UserAuthResponse.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .role(Role.USER)
                .build();

        when(authService.getCurrentUser("Bearer token")).thenReturn(response);

        mockMvc.perform(get("/v1/api/auth/me")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void logout_ShouldReturnOk_WhenRequestIsValid() throws Exception {
        LogoutRequest request = LogoutRequest.builder().refreshToken("refresh-token").build();
        ApiResponse response = ApiResponse.builder().success(true).message("Başarıyla çıkış yapıldı.").build();

        when(authService.logout(any(LogoutRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void changePassword_ShouldReturnBadRequest_WhenBodyValidationFails() throws Exception {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("Current123")
                .newPassword("weak")
                .confirmPassword("weak")
                .build();

        mockMvc.perform(post("/v1/api/auth/change-password")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).changePassword(any(), any(ChangePasswordRequest.class));
    }
}

