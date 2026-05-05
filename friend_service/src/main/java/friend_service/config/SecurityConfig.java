package friend_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for friend-service.
 *
 * Development mode:
 * - All endpoints are permitted to simplify local testing.
 * - CSRF is disabled (stateless JWT-based service).
 * - Stateless session management.
 *
 * TODO (Production):
 * - Enable JWT resource server: httpSecurity.oauth2ResourceServer(...)
 * - Restrict /v1/api/friends/** to authenticated users only
 * - Restrict /v1/api/friends/internal/** to service-to-service authentication
 *   (e.g., require a specific role or use mutual TLS / API key validation)
 * - Example:
 *     .requestMatchers("/v1/api/friends/internal/**").hasRole("SERVICE")
 *     .anyRequest().authenticated()
 *
 * NOTE: Block/unblock endpoints do NOT exist in this service.
 * Block operations are managed exclusively by block-service.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            // Actuator health check (for Consul)
            "/actuator/**",
            // Swagger / OpenAPI
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            // Internal endpoints (service-to-service communication)
            "/v1/api/friends/internal/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public paths always accessible
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        // TODO (Production): Replace permitAll with .authenticated()
                        // and configure JWT resource server
                        .anyRequest().permitAll()
                );

        /*
         * TODO (Production): Enable JWT Resource Server
         * http.oauth2ResourceServer(oauth2 -> oauth2
         *     .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
         * );
         */

        return http.build();
    }
}
