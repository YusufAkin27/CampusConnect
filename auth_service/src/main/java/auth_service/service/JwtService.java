package auth_service.service;

import auth_service.entity.User;
import io.jsonwebtoken.Claims;

import java.util.Map;
import java.util.function.Function;

/**
 * JWT token operasyonlarının servis arayüzü.
 */
public interface JwtService {

    String generateAccessToken(User user);

    String generateAccessToken(User user, Map<String, Object> extraClaims);

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    boolean isTokenValid(String token, User user);

    boolean isTokenExpired(String token);

    long getAccessTokenExpiration();
}
