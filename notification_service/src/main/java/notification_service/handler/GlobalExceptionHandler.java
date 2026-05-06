package notification_service.handler;

import notification_service.dto.response.ApiResponse;
import notification_service.exception.ExternalServiceUnavailableException;
import notification_service.exception.InvalidNotificationTypeException;
import notification_service.exception.NotificationAccessDeniedException;
import notification_service.exception.NotificationDeliveryFailedException;
import notification_service.exception.NotificationNotFoundException;
import notification_service.exception.NotificationPreferenceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder("Validation failed");
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            message.append(". ").append(error.getField()).append(": ").append(error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.failure(message.toString(), null));
    }

    @ExceptionHandler({
        NotificationNotFoundException.class,
        NotificationPreferenceNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.failure(ex.getMessage(), null));
    }

    @ExceptionHandler({
        NotificationAccessDeniedException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleForbidden(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.failure(ex.getMessage(), null));
    }

    @ExceptionHandler({
        InvalidNotificationTypeException.class,
        NotificationDeliveryFailedException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.failure(ex.getMessage(), null));
    }

    @ExceptionHandler({
        ExternalServiceUnavailableException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleExternal(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ApiResponse.failure(ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.failure("Unexpected error", null));
    }
}
