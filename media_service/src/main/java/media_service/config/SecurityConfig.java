package media_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for media-service.
 *
 * Development mode: All endpoints are open for ease of testing.
 *
 * TODO (Production):
 *  - Upload endpoints (/v1/api/media/upload/**) should require authentication:
 *      .requestMatchers("/v1/api/media/upload/**").authenticated()
 *  - Internal endpoints (/v1/api/media/internal/**) should be protected
 *    by API key or mTLS for service-to-service communication.
 *  - Public static files (/uploads/**) should ideally be served via CDN or S3
 *    rather than directly from this service.
 *  - Enable JWT resource server authentication:
 *      .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Actuator health check - always public
                .requestMatchers("/actuator/**").permitAll()
                // Swagger / OpenAPI - public in development
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                // Static uploaded files - publicly accessible
                .requestMatchers("/uploads/**").permitAll()
                // Internal endpoints - permit all in development
                // TODO (Production): Protect with service-to-service auth
                .requestMatchers("/v1/api/media/internal/**").permitAll()
                // All other endpoints - permit all in development
                // TODO (Production): Change to .authenticated() and enable JWT
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
