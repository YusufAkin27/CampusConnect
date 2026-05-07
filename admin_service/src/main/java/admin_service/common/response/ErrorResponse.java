package admin_service.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standardized error response for all exception handlers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private boolean success;

    private String message;

    private String errorCode;

    private int status;

    private String path;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
