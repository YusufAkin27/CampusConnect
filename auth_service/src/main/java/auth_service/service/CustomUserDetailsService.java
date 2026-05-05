package auth_service.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Username veya email ile kullanıcı yükleme desteği sunan servis arayüzü.
 */
public interface CustomUserDetailsService extends UserDetailsService {

    UserDetails loadUserByUsernameOrEmail(String usernameOrEmail);
}
