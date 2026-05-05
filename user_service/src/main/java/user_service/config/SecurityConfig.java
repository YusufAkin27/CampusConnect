package user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for user-service.
 *
 * <p><b>Current mode:</b> Development-friendly setup.
 * All endpoints are permitted to ease local testing without a running auth-service.
 *
 * <p><b>TODO (Production):</b>
 * <ul>
 *   <li>Enable {@code .oauth2ResourceServer()} with JWT validation.</li>
 *   <li>Remove {@code .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())}.</li>
 *   <li>Restrict internal endpoints ({@code /v1/api/users/internal/**}) with IP filtering or service-to-service auth.</li>
 *   <li>Restrict admin endpoints ({@code /v1/api/users/admin/**}) to ROLE_ADMIN.</li>
 * </ul>
 *
 * <p>Example production config:
 * <pre>
 *   .authorizeHttpRequests(auth -> auth
 *       .requestMatchers("/actuator/health").permitAll()
 *       .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
 *       .requestMatchers("/v1/api/users/profiles").permitAll() // for auth-service to create profile
 *       .requestMatchers("/v1/api/users/internal/**").permitAll() // TODO: restrict to service-to-service
 *       .requestMatchers("/v1/api/users/admin/**").hasRole("ADMIN")
 *       .anyRequest().authenticated()
 *   )
 *   .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
 * </pre>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless REST API
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless session - no server-side sessions
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ============================================================
                // DEVELOPMENT MODE: All requests permitted for local testing.
                // TODO: Replace with production authorization rules (see class JavaDoc).
                // ============================================================
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll()
                );

        return http.build();
    }
}
