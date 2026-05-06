package api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.Map;

public class JwtUtils {

    private final Key key;

    public JwtUtils(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
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
