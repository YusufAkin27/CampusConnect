package api_gateway.config;

import api_gateway.resolver.IpKeyResolver;
import api_gateway.resolver.UserIdKeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder, UserIdKeyResolver userIdKeyResolver,
                                    IpKeyResolver ipKeyResolver) {
        return builder.routes()
            .route("auth-login", r -> r.path("/v1/api/auth/login", "/v1/api/auth/register")
                .filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(new org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter(5, 10))
                    .setKeyResolver(ipKeyResolver)))
                .uri("lb://auth-service"))
            .route("auth-refresh", r -> r.path("/v1/api/auth/refresh-token")
                .filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(new org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter(20, 40))
                    .setKeyResolver(ipKeyResolver)))
                .uri("lb://auth-service"))
            .build();
    }
}
