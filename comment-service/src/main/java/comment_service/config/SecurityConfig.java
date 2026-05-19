package comment_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for comment-service.
 *
 * Development mode:
 * - Public endpoints (health, docs) are always accessible.
 * - Other endpoints are permitted for local testing.
 * - CSRF disabled (stateless JWT-based service).
 * - Stateless session management.
 *
 * TODO (Production):
 * - Enable JWT resource server: httpSecurity.oauth2ResourceServer(...)
 * - Restrict /v1/api/comments/** to authenticated users
 * - Restrict /internal/comments/** to service-to-service auth
 *   (e.g. X-Internal-Secret header or mutual TLS)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            // Actuator health check (for Consul)
            "/actuator/**",
            // Scalar / OpenAPI
            "/scalar/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            // Internal endpoints (service-to-service communication)
            "/internal/comments/**"
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
