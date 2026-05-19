package logging_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Logging Service.
 *
 * Development phase: All endpoints are open for easy integration testing.
 *
 * TODO Production security:
 * - /v1/api/logs/query/** -> require ROLE_ADMIN
 * - /v1/api/logs/stats/** -> require ROLE_ADMIN
 * - /v1/api/logs/errors/** -> require ROLE_ADMIN
 * - /v1/api/logs/security/** -> require ROLE_ADMIN
 * - /v1/api/logs/audit/** -> require ROLE_ADMIN
 * - /v1/api/logs/retention/** -> require ROLE_ADMIN
 * - /v1/api/logs/internal/** -> require service-to-service auth (X-Internal-Key header or mTLS)
 * - /v1/api/logs/** (ingestion) -> require service role or API key
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Actuator health check - always public
                        .requestMatchers("/actuator/**").permitAll()
                        // Scalar/OpenAPI - public for development
                        .requestMatchers("/scalar/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/v3/api-docs/**", "/webjars/**").permitAll()
                        // All logging endpoints - permitAll for development
                        // TODO: Restrict in production as described above
                        .requestMatchers("/v1/api/logs/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
