package auth_service.service.impl;

import auth_service.entity.Role;
import auth_service.entity.User;
import auth_service.exception.UserNotFoundException;
import auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        User user = baseUser();
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("john_doe");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void loadUserByUsername_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("missing"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Kullanıcı bulunamadı");
    }

    @Test
    void loadUserByUsernameOrEmail_ShouldReturnUserDetails_WhenUserExists() {
        User user = baseUser();
        when(userRepository.findByUsernameOrEmail("john@example.com", "john@example.com"))
                .thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsernameOrEmail("john@example.com");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void loadUserByUsernameOrEmail_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findByUsernameOrEmail("missing", "missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsernameOrEmail("missing"))
                .isInstanceOf(UserNotFoundException.class);
    }

    private User baseUser() {
        return User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .password("encoded")
                .role(Role.USER)
                .enabled(true)
                .accountNonLocked(true)
                .build();
    }
}

