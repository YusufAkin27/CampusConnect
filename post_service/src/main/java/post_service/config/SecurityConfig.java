package post_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for post-service.
 *
 * DEVELOPMENT MODE:
 * All endpoints are permitted to simplify local development and testing.
 *
 * PRODUCTION TODO:
 * - Enable JWT-based authentication via Spring Security OAuth2 Resource Server.
 * - Restrict /admin/** endpoints to users with ROLE_ADMIN.
 * - Restrict /internal/** endpoints to service-to-service authentication only.
 * - Enable HTTPS and configure CORS properly.
 *
 * Example production config (commented out below):
 *
 *  http.authorizeHttpRequests(auth -> auth
 *      .requestMatchers("/actuator/health").permitAll()
 *      .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
 *      .requestMatchers("/v1/api/posts/internal/**").permitAll() // or use API Key
 *      .requestMatchers("/v1/api/posts/reports/admin/**").hasRole("ADMIN")
 *      .anyRequest().authenticated()
 *  )
 *  .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
 *  .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Development: allow all requests
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
