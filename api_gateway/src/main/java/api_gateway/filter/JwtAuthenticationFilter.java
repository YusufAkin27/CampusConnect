package api_gateway.filter;

import api_gateway.util.JwtUtils;
import api_gateway.util.PathUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_PATHS = List.of(
        "/v1/api/auth/login",
        "/v1/api/auth/register",
        "/v1/api/auth/refresh-token",
        "/v1/api/auth/forgot-password",
        "/v1/api/auth/reset-password",
        "/v1/api/auth/verify-email",
        "/v1/api/auth/test",
        "/actuator/health",
        "/actuator/info",
        "/v3/api-docs",
        "/swagger-ui",
        "/swagger-ui.html",
        "/scalar",
        "/docs",
        "/socket.io"
    );

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (PathUtils.matchesAny(path, PUBLIC_PATHS)) {
            return chain.filter(exchange);
        }

        if (jwtSecret == null || jwtSecret.isBlank()) {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7).trim();
        try {
            JwtUtils jwtUtils = new JwtUtils(jwtSecret);
            Claims claims = jwtUtils.parse(token);
            String userId = JwtUtils.getStringClaim(claims, "userId");
            String username = JwtUtils.getStringClaim(claims, "username");
            String fullName = JwtUtils.getStringClaim(claims, "fullName");
            List<String> roles = JwtUtils.getRoles(claims, "roles");

            ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove("X-User-Id");
                    headers.remove("X-Username");
                    headers.remove("X-Full-Name");
                    headers.remove("X-User-Roles");
                    headers.add("X-User-Id", userId);
                    headers.add("X-Username", username);
                    headers.add("X-Full-Name", fullName);
                    headers.add("X-User-Roles", String.join(",", roles));
                })
                .build();
            return chain.filter(exchange.mutate().request(request).build());
        } catch (ExpiredJwtException ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        } catch (JwtException ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -7;
    }
}
