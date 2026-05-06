package event_service.handler;

import event_service.dto.response.ApiResponse;
import event_service.exception.EventAlreadyCancelledException;
import event_service.exception.EventAlreadyCompletedException;
import event_service.exception.EventAlreadyJoinedException;
import event_service.exception.EventAlreadyStartedException;
import event_service.exception.EventCapacityFullException;
import event_service.exception.EventDateInvalidException;
import event_service.exception.EventNotFoundException;
import event_service.exception.EventNotJoinedException;
import event_service.exception.EventPermissionDeniedException;
import event_service.exception.EventRegistrationClosedException;
import event_service.exception.FavoriteAlreadyExistsException;
import event_service.exception.FavoriteNotFoundException;
import event_service.exception.MediaValidationFailedException;
import event_service.exception.ParticipantAlreadyApprovedException;
import event_service.exception.ParticipantAlreadyRejectedException;
import event_service.exception.ParticipantNotFoundException;
import jakarta.validation.ConstraintViolationException;
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.failure(ex.getMessage(), null));
    }

    @ExceptionHandler({
        EventNotFoundException.class,
        ParticipantNotFoundException.class,
        FavoriteNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.failure(ex.getMessage(), null));
    }

    @ExceptionHandler({
        EventPermissionDeniedException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleForbidden(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.failure(ex.getMessage(), null));
    }

    @ExceptionHandler({
        EventAlreadyCancelledException.class,
        EventAlreadyCompletedException.class,
        EventCapacityFullException.class,
        EventRegistrationClosedException.class,
        EventAlreadyStartedException.class,
        EventAlreadyJoinedException.class,
        EventNotJoinedException.class,
        ParticipantAlreadyApprovedException.class,
        ParticipantAlreadyRejectedException.class,
        FavoriteAlreadyExistsException.class,
        MediaValidationFailedException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.failure(ex.getMessage(), null));
    }

    @ExceptionHandler({
        EventDateInvalidException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.failure(ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.failure("Unexpected error", null));
    }
}
