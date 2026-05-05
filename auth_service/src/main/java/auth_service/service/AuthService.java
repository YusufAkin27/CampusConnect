package auth_service.service;

import auth_service.dto.request.*;
import auth_service.dto.response.*;

/**
 * Kimlik doğrulama işlemlerinin servis arayüzü.
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    TokenResponse refreshToken(RefreshTokenRequest request);

    ApiResponse logout(LogoutRequest request);

    UserAuthResponse getCurrentUser(String token);

    UserAuthResponse validateToken(String token);

    ApiResponse changePassword(String token, ChangePasswordRequest request);
}
