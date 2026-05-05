package user_service.exception.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard error response format for all API errors.
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
