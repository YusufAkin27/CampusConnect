package api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;

public class JwtUtils {

    private final SecretKey key;

    public JwtUtils(String secret) {
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException e) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        // Ensure minimum 256-bit key for HMAC-SHA256
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            keyBytes = padded;
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public static String getStringClaim(Claims claims, String name) {
        Object value = claims.get(name);
        return value == null ? null : String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    public static List<String> getRoles(Claims claims, String name) {
        Object value = claims.get(name);
        if (value instanceof List<?>) {
            return ((List<?>) value).stream().map(String::valueOf).toList();
        }
        if (value instanceof String) {
            return List.of(((String) value).split(","));
        }
        if (value instanceof Map<?, ?> map) {
            return map.values().stream().map(String::valueOf).toList();
        }
        return List.of();
    }
}
