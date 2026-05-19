package like_service.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * API Gateway'den gelen kullanıcı bilgilerini header üzerinden okur.
 *
 * Headerlar:
 * - X-User-Id
 * - X-User-Email
 * - X-User-Role
 */
@Slf4j
@Component
public class CurrentUserResolver {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_EMAIL = "X-User-Email";
    private static final String HEADER_USER_ROLE = "X-User-Role";

    /**
     * Mevcut kullanıcının UUID'sini döner.
     *
     * @return Kullanıcının UUID'si
     * @throws IllegalArgumentException X-User-Id header'ı bulunamazsa veya geçersizse
     */
    public UUID getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        String userIdHeader = request.getHeader(HEADER_USER_ID);

        if (userIdHeader == null || userIdHeader.isBlank()) {
            log.warn("X-User-Id header bulunamadı");
            throw new IllegalArgumentException("Kimlik doğrulanamadı. X-User-Id header eksik.");
        }

        try {
            UUID userId = UUID.fromString(userIdHeader);
            log.debug("Mevcut kullanıcı ID: {}", userId);
            return userId;
        } catch (IllegalArgumentException e) {
            log.warn("Geçersiz X-User-Id formatı: {}", userIdHeader);
            throw new IllegalArgumentException("Geçersiz kullanıcı ID formatı: " + userIdHeader);
        }
    }

    /**
     * Mevcut kullanıcının email adresini döner.
     *
     * @return Kullanıcının email adresi, yoksa null
     */
    public String getCurrentUserEmail() {
        HttpServletRequest request = getCurrentRequest();
        return request.getHeader(HEADER_USER_EMAIL);
    }

    /**
     * Mevcut kullanıcının rolünü döner.
     *
     * @return Kullanıcının rolü, yoksa null
     */
    public String getCurrentUserRole() {
        HttpServletRequest request = getCurrentRequest();
        return request.getHeader(HEADER_USER_ROLE);
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("HTTP request context bulunamadı.");
        }
        return attributes.getRequest();
    }
}
