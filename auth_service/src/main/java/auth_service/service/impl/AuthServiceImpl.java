package auth_service.service.impl;

import auth_service.dto.request.*;
import auth_service.dto.response.*;
import auth_service.entity.RefreshToken;
import auth_service.entity.Role;
import auth_service.entity.User;
import auth_service.exception.*;
import auth_service.mapper.UserMapper;
import auth_service.repository.RefreshTokenRepository;
import auth_service.repository.UserRepository;
import auth_service.service.AuthService;
import auth_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // ── Register ─────────────────────────────────────────────────

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Register isteği. Username: {}, Email: {}", request.getUsername(), request.getEmail());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw UserAlreadyExistsException.withUsername(request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw UserAlreadyExistsException.withEmail(request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        user = userRepository.save(user);
        log.info("Yeni kullanıcı kaydedildi. ID: {}", user.getId());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = createAndSaveRefreshToken(user);

        return buildAuthResponse(accessToken, refreshToken, user);
    }

    // ── Login ────────────────────────────────────────────────────

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login isteği. UsernameOrEmail: {}", request.getUsernameOrEmail());

        User user = userRepository.findByUsernameOrEmail(
                        request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> UserNotFoundException.withIdentifier(request.getUsernameOrEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Hatalı şifre girişi. Username: {}", user.getUsername());
            throw new InvalidPasswordException();
        }

        refreshTokenRepository.revokeAllUserTokens(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = createAndSaveRefreshToken(user);

        log.info("Başarılı login. Username: {}", user.getUsername());
        return buildAuthResponse(accessToken, refreshToken, user);
    }

    // ── Refresh Token ─────────────────────────────────────────────

    @Override
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refresh token isteği.");

        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Refresh token bulunamadı."));

        if (storedToken.isExpired()) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
            throw new RefreshTokenExpiredException();
        }

        if (storedToken.isRevoked()) {
            throw new InvalidTokenException("Bu refresh token daha önce iptal edilmiş.");
        }

        User user = storedToken.getUser();
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = createAndSaveRefreshToken(user);

        log.info("Token yenilendi. Username: {}", user.getUsername());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    // ── Logout ───────────────────────────────────────────────────

    @Override
    @Transactional
    public ApiResponse logout(LogoutRequest request) {
        log.info("Logout isteği.");

        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Refresh token bulunamadı."));

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        log.info("Logout başarılı. User: {}", storedToken.getUser().getUsername());

        return ApiResponse.builder()
                .success(true)
                .message("Başarıyla çıkış yapıldı.")
                .build();
    }

    // ── Me ───────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public UserAuthResponse getCurrentUser(String token) {
        String username = jwtService.extractUsername(extractBearerToken(token));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> UserNotFoundException.withIdentifier(username));
        return userMapper.toUserAuthResponse(user);
    }

    // ── Validate Token ───────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public UserAuthResponse validateToken(String token) {
        try {
            String cleanToken = extractBearerToken(token);

            if (jwtService.isTokenExpired(cleanToken)) {
                throw new InvalidTokenException("Token süresi dolmuş.");
            }

            String username = jwtService.extractUsername(cleanToken);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> UserNotFoundException.withIdentifier(username));

            if (!jwtService.isTokenValid(cleanToken, user)) {
                throw new InvalidTokenException("Token geçersiz.");
            }

            return userMapper.toUserAuthResponse(user);
        } catch (InvalidTokenException | UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Token doğrulama hatası: {}", e.getMessage());
            throw new InvalidTokenException("Token doğrulanamadı.");
        }
    }

    // ── Change Password ──────────────────────────────────────────

    @Override
    @Transactional
    public ApiResponse changePassword(String token, ChangePasswordRequest request) {
        String username = jwtService.extractUsername(extractBearerToken(token));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> UserNotFoundException.withIdentifier(username));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Mevcut şifre hatalı.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidPasswordException("Yeni şifre ve tekrar şifresi eşleşmiyor.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        refreshTokenRepository.revokeAllUserTokens(user);

        log.info("Şifre değiştirildi. Username: {}", user.getUsername());

        return ApiResponse.builder()
                .success(true)
                .message("Şifre başarıyla değiştirildi. Lütfen tekrar giriş yapınız.")
                .build();
    }

    // ── Yardımcı Metodlar ────────────────────────────────────────

    private String createAndSaveRefreshToken(User user) {
        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .expiryDate(expiryDate)
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, User user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(userMapper.toUserAuthResponse(user))
                .build();
    }

    private String extractBearerToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}
