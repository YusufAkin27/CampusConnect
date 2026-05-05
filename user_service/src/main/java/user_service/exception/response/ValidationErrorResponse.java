package user_service.exception.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Error response for Bean Validation failures.
 * Contains a map of field name -> error message pairs.
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
