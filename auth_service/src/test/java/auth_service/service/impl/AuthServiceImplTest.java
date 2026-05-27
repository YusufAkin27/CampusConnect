package auth_service.service.impl;

import auth_service.dto.request.ChangePasswordRequest;
import auth_service.dto.request.LoginRequest;
import auth_service.dto.request.LogoutRequest;
import auth_service.dto.request.RefreshTokenRequest;
import auth_service.dto.request.RegisterRequest;
import auth_service.dto.response.ApiResponse;
import auth_service.dto.response.AuthResponse;
import auth_service.dto.response.TokenResponse;
import auth_service.dto.response.UserAuthResponse;
import auth_service.entity.RefreshToken;
import auth_service.entity.Role;
import auth_service.entity.User;
import auth_service.exception.InvalidPasswordException;
import auth_service.exception.InvalidTokenException;
import auth_service.exception.RefreshTokenExpiredException;
import auth_service.exception.UserAlreadyExistsException;
import auth_service.exception.UserNotFoundException;
import auth_service.mapper.UserMapper;
import auth_service.repository.RefreshTokenRepository;
import auth_service.repository.UserRepository;
import auth_service.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "refreshTokenExpiration", 604_800_000L);
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenRequestIsValid() {
        RegisterRequest request = RegisterRequest.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("Password123")
                .build();

        User savedUser = baseUser();
        UserAuthResponse mapped = mappedUser();

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateAccessToken(savedUser)).thenReturn("access-token");
        when(jwtService.getAccessTokenExpiration()).thenReturn(900_000L);
        when(userMapper.toUserAuthResponse(savedUser)).thenReturn(mapped);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse response = authService.register(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isNotBlank();
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(900_000L);
        assertThat(response.getUser()).isEqualTo(mapped);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-pass");
        assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.USER);

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void register_ShouldThrowUserAlreadyExistsException_WhenUsernameAlreadyExists() {
        RegisterRequest request = RegisterRequest.builder().username("john_doe").email("john@example.com").build();
        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Kullanıcı adı zaten kullanımda");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ShouldThrowUserAlreadyExistsException_WhenEmailAlreadyExists() {
        RegisterRequest request = RegisterRequest.builder().username("john_doe").email("john@example.com").build();
        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Email adresi zaten kayıtlı");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ShouldThrowNullPointerException_WhenRequestIsNull() {
        assertThatThrownBy(() -> authService.register(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        LoginRequest request = LoginRequest.builder()
                .usernameOrEmail("john_doe")
                .password("Password123")
                .build();

        User user = baseUser();
        UserAuthResponse mapped = mappedUser();

        when(userRepository.findByUsernameOrEmail("john_doe", "john_doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", user.getPassword())).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.getAccessTokenExpiration()).thenReturn(900_000L);
        when(userMapper.toUserAuthResponse(user)).thenReturn(mapped);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isNotBlank();
        verify(refreshTokenRepository).revokeAllUserTokens(user);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void login_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        LoginRequest request = LoginRequest.builder().usernameOrEmail("missing").password("Password123").build();
        when(userRepository.findByUsernameOrEmail("missing", "missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Kullanıcı bulunamadı");

        verify(refreshTokenRepository, never()).revokeAllUserTokens(any(User.class));
    }

    @Test
    void login_ShouldThrowInvalidPasswordException_WhenPasswordIsWrong() {
        LoginRequest request = LoginRequest.builder().usernameOrEmail("john_doe").password("wrong").build();
        User user = baseUser();

        when(userRepository.findByUsernameOrEmail("john_doe", "john_doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request)).isInstanceOf(InvalidPasswordException.class);
        verify(refreshTokenRepository, never()).revokeAllUserTokens(any(User.class));
    }

    @Test
    void refreshToken_ShouldReturnNewTokens_WhenStoredTokenIsValid() {
        User user = baseUser();
        RefreshToken token = RefreshToken.builder()
                .token("old-token")
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("old-token")).thenReturn(Optional.of(token));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateAccessToken(user)).thenReturn("new-access");
        when(jwtService.getAccessTokenExpiration()).thenReturn(900_000L);

        TokenResponse response = authService.refreshToken(RefreshTokenRequest.builder().refreshToken("old-token").build());

        assertThat(response.getAccessToken()).isEqualTo("new-access");
        assertThat(response.getRefreshToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotEqualTo("old-token");
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void refreshToken_ShouldThrowInvalidTokenException_WhenTokenDoesNotExist() {
        when(refreshTokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken(RefreshTokenRequest.builder().refreshToken("missing").build()))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Refresh token bulunamadı");
    }

    @Test
    void refreshToken_ShouldThrowRefreshTokenExpiredException_WhenTokenIsExpired() {
        RefreshToken token = RefreshToken.builder()
                .token("expired")
                .user(baseUser())
                .expiryDate(LocalDateTime.now().minusMinutes(1))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("expired")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.refreshToken(RefreshTokenRequest.builder().refreshToken("expired").build()))
                .isInstanceOf(RefreshTokenExpiredException.class);

        assertThat(token.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void refreshToken_ShouldThrowInvalidTokenException_WhenTokenIsAlreadyRevoked() {
        RefreshToken token = RefreshToken.builder()
                .token("revoked")
                .user(baseUser())
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .revoked(true)
                .build();

        when(refreshTokenRepository.findByToken("revoked")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.refreshToken(RefreshTokenRequest.builder().refreshToken("revoked").build()))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("iptal edilmiş");
    }

    @Test
    void logout_ShouldRevokeTokenAndReturnSuccess_WhenTokenExists() {
        User user = baseUser();
        RefreshToken token = RefreshToken.builder()
                .token("logout-token")
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("logout-token")).thenReturn(Optional.of(token));
        when(refreshTokenRepository.save(token)).thenReturn(token);

        ApiResponse response = authService.logout(LogoutRequest.builder().refreshToken("logout-token").build());

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).contains("çıkış");
        assertThat(token.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void logout_ShouldThrowInvalidTokenException_WhenTokenDoesNotExist() {
        when(refreshTokenRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.logout(LogoutRequest.builder().refreshToken("missing").build()))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void getCurrentUser_ShouldReturnMappedUser_WhenTokenAndUserAreValid() {
        User user = baseUser();
        UserAuthResponse mapped = mappedUser();

        when(jwtService.extractUsername("token-123")).thenReturn("john_doe");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(userMapper.toUserAuthResponse(user)).thenReturn(mapped);

        UserAuthResponse response = authService.getCurrentUser("Bearer token-123");

        assertThat(response).isEqualTo(mapped);
    }

    @Test
    void getCurrentUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(jwtService.extractUsername("token-123")).thenReturn("unknown");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getCurrentUser("Bearer token-123"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void validateToken_ShouldReturnMappedUser_WhenTokenIsValid() {
        User user = baseUser();
        UserAuthResponse mapped = mappedUser();

        when(jwtService.isTokenExpired("valid-token")).thenReturn(false);
        when(jwtService.extractUsername("valid-token")).thenReturn("john_doe");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid("valid-token", user)).thenReturn(true);
        when(userMapper.toUserAuthResponse(user)).thenReturn(mapped);

        UserAuthResponse response = authService.validateToken("Bearer valid-token");

        assertThat(response).isEqualTo(mapped);
    }

    @Test
    void validateToken_ShouldThrowInvalidTokenException_WhenTokenExpired() {
        when(jwtService.isTokenExpired("expired-token")).thenReturn(true);

        assertThatThrownBy(() -> authService.validateToken("expired-token"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("süresi dolmuş");
    }

    @Test
    void validateToken_ShouldThrowInvalidTokenException_WhenJwtServiceSaysInvalid() {
        User user = baseUser();

        when(jwtService.isTokenExpired("invalid-token")).thenReturn(false);
        when(jwtService.extractUsername("invalid-token")).thenReturn("john_doe");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid("invalid-token", user)).thenReturn(false);

        assertThatThrownBy(() -> authService.validateToken("invalid-token"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("geçersiz");
    }

    @Test
    void validateToken_ShouldWrapUnexpectedExceptions_WhenUnhandledErrorOccurs() {
        when(jwtService.isTokenExpired("boom")).thenThrow(new IllegalStateException("boom"));

        assertThatThrownBy(() -> authService.validateToken("boom"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("doğrulanamadı");
    }

    @Test
    void changePassword_ShouldUpdatePasswordAndRevokeTokens_WhenCurrentPasswordIsCorrect() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("Current123")
                .newPassword("NewPassword123")
                .confirmPassword("NewPassword123")
                .build();

        User user = baseUser();

        when(jwtService.extractUsername("token-123")).thenReturn("john_doe");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Current123", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("NewPassword123")).thenReturn("encoded-new");

        ApiResponse response = authService.changePassword("Bearer token-123", request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(user.getPassword()).isEqualTo("encoded-new");
        verify(userRepository).save(user);
        verify(refreshTokenRepository).revokeAllUserTokens(user);
    }

    @Test
    void changePassword_ShouldThrowInvalidPasswordException_WhenCurrentPasswordIsIncorrect() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("wrong")
                .newPassword("NewPassword123")
                .confirmPassword("NewPassword123")
                .build();
        User user = baseUser();

        when(jwtService.extractUsername("token")).thenReturn("john_doe");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword("token", request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Mevcut şifre");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_ShouldThrowInvalidPasswordException_WhenNewPasswordsDoNotMatch() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("Current123")
                .newPassword("NewPassword123")
                .confirmPassword("Mismatch123")
                .build();
        User user = baseUser();

        when(jwtService.extractUsername("token")).thenReturn("john_doe");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Current123", user.getPassword())).thenReturn(true);

        assertThatThrownBy(() -> authService.changePassword("token", request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("eşleşmiyor");

        verify(userRepository, never()).save(any(User.class));
    }

    private User baseUser() {
        return User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .password("encoded-pass")
                .role(Role.USER)
                .enabled(true)
                .accountNonLocked(true)
                .build();
    }

    private UserAuthResponse mappedUser() {
        return UserAuthResponse.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .role(Role.USER)
                .build();
    }
}

