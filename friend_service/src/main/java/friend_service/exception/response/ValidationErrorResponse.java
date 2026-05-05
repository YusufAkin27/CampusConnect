package friend_service.exception.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Extended error response used specifically for @Valid bean validation failures.
 * Contains a map of field names to their validation error messages.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {

    private boolean success;
    private String message;
    private Map<String, String> validationErrors;
    private int status;
    private String path;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
