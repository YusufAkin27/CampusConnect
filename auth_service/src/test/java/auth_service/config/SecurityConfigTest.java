package auth_service.config;

import auth_service.entity.Role;
import auth_service.entity.User;
import auth_service.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationConfiguration authenticationConfiguration;
    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    void authenticationProvider_ShouldUseCustomUserDetailsServiceAndPasswordEncoder() {
        SecurityConfig securityConfig = new SecurityConfig(jwtAuthenticationFilter, customUserDetailsService, passwordEncoder);

        AuthenticationProvider provider = securityConfig.authenticationProvider();

        assertThat(provider).isInstanceOf(DaoAuthenticationProvider.class);
        DaoAuthenticationProvider daoProvider = (DaoAuthenticationProvider) provider;

        User user = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .password("encoded")
                .role(Role.USER)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        when(customUserDetailsService.loadUserByUsername("john_doe")).thenReturn(user);
        when(passwordEncoder.matches("plain", "encoded")).thenReturn(true);

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken("john_doe", "plain");

        var authResult = daoProvider.authenticate(authRequest);

        assertThat(authResult.isAuthenticated()).isTrue();
        assertThat(authResult.getPrincipal()).isEqualTo(user);
    }

    @Test
    void authenticationManager_ShouldReturnManagerFromConfiguration() throws Exception {
        SecurityConfig securityConfig = new SecurityConfig(jwtAuthenticationFilter, customUserDetailsService, passwordEncoder);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        assertThat(result).isEqualTo(authenticationManager);
    }
}

