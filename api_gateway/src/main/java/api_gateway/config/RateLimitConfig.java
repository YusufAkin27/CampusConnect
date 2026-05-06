package api_gateway.config;

import api_gateway.resolver.IpKeyResolver;
import api_gateway.resolver.UserIdKeyResolver;
import java.util.Map;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Bean
    public RedisRateLimiter defaultRateLimiter() {
        return new RedisRateLimiter(60, 120);
    }

    @Bean
    public RedisRateLimiter authLoginRateLimiter() {
        return new RedisRateLimiter(5, 10);
    }

    @Bean
    public RedisRateLimiter authRefreshRateLimiter() {
        return new RedisRateLimiter(20, 40);
    }

    @Bean
    public UserIdKeyResolver userIdKeyResolver() {
        return new UserIdKeyResolver();
    }

    @Bean
    public IpKeyResolver ipKeyResolver() {
        return new IpKeyResolver();
    }
}
