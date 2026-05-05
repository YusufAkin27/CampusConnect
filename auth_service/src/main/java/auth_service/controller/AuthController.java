package auth_service.controller;

import auth_service.dto.request.*;
import auth_service.dto.response.*;
import auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Auth Controller - Tüm kimlik doğrulama endpoint'leri.
 * Base path: /v1/api/auth
 */
@Slf4j
@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "Kimlik doğrulama ve token yönetim işlemleri")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Yeni kullanıcı kaydı",
            description = "Yeni kullanıcı oluşturur, access + refresh token döner.")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /register - Username: {}", request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Kullanıcı girişi",
            description = "Username veya email + şifre ile giriş yapar.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /login - UsernameOrEmail: {}", request.getUsernameOrEmail());
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Token yenileme",
            description = "Geçerli refresh token ile yeni access + refresh token üretir.")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("POST /refresh-token");
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Kullanıcı çıkışı",
            description = "Refresh token'ı geçersiz hale getirir.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ApiResponse> logout(@Valid @RequestBody LogoutRequest request) {
        log.info("POST /logout");
        return ResponseEntity.ok(authService.logout(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Mevcut kullanıcı bilgisi",
            description = "Authorization header'daki token'dan kullanıcı bilgisi döner.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<UserAuthResponse> getCurrentUser(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        log.info("GET /me");
        return ResponseEntity.ok(authService.getCurrentUser(authHeader));
    }

    @PostMapping("/validate-token")
    @Operation(summary = "Token doğrulama",
            description = "API Gateway ve mikroservisler için token doğrulama endpoint'i.")
    public ResponseEntity<UserAuthResponse> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "token", required = false) String tokenParam) {
        log.info("POST /validate-token");

        String token = (authHeader != null) ? authHeader : tokenParam;
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(authService.validateToken(token));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Şifre değiştirme",
            description = "Giriş yapmış kullanıcının şifresini değiştirir.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ApiResponse> changePassword(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("POST /change-password");
        return ResponseEntity.ok(authService.changePassword(authHeader, request));
    }
}
