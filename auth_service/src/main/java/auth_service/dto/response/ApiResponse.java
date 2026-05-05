package auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Genel API yanıt DTO'su.
 * Logout, change-password gibi işlemlerin yanıtı için kullanılır.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private boolean success;
    private String message;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
