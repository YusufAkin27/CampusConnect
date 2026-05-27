package auth_service.security;

import auth_service.dto.response.UserAuthResponse;
import auth_service.entity.Role;
import auth_service.entity.User;
import auth_service.repository.UserRepository;
import auth_service.service.AuthService;
import auth_service.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.cloud.consul.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:auth_security_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class AuthSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void publicEndpoints_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        String registerBody = objectMapper.writeValueAsString(java.util.Map.of(
                "username", "john_doe",
                "email", "john@example.com",
                "password", "Password123"
        ));

        mockMvc.perform(post("/v1/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of(
                                "usernameOrEmail", "john_doe",
                                "password", "Password123"
                        ))))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_ShouldReturnForbidden_WhenNoAuthenticationPresent() throws Exception {
        mockMvc.perform(get("/v1/api/auth/me").header("Authorization", "Bearer token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpoint_ShouldReturnOk_WhenAuthenticatedPrincipalExists() throws Exception {
        User user = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .password("encoded")
                .role(Role.USER)
                .enabled(true)
                .accountNonLocked(true)
                .build();

        when(jwtService.extractUsername("valid-token")).thenReturn("john_doe");
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid("valid-token", user)).thenReturn(true);
        when(authService.getCurrentUser("Bearer valid-token")).thenReturn(
                UserAuthResponse.builder()
                        .id(1L)
                        .username("john_doe")
                        .email("john@example.com")
                        .role(Role.USER)
                        .build()
        );

        mockMvc.perform(get("/v1/api/auth/me")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk());
    }
}

