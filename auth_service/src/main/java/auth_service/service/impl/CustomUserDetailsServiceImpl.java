package auth_service.service.impl;

import auth_service.exception.UserNotFoundException;
import auth_service.repository.UserRepository;
import auth_service.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.debug("Loading user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> UserNotFoundException.withIdentifier(username));
    }

    @Override
    public UserDetails loadUserByUsernameOrEmail(String usernameOrEmail) {
        log.debug("Loading user by username or email: {}", usernameOrEmail);
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> UserNotFoundException.withIdentifier(usernameOrEmail));
    }
}
