package like_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Actuator health endpoint
                        .requestMatchers("/actuator/**").permitAll()
                        // Scalar / OpenAPI docs
                        .requestMatchers("/scalar/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        // Internal endpoints (servisler arası iletişim)
                        // İleride X-Internal-Secret veya service-to-service token ile korunabilir
                        .requestMatchers("/internal/**").permitAll()
                        // Diğer tüm endpointler authenticated olmalı
                        // API Gateway JWT doğrulamasını yapar ve header'ları iletir
                        // Burada permitAll kullanıyoruz çünkü auth gateway seviyesinde yapılıyor
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
