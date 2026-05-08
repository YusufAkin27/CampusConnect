package story_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import story_service.dto.response.ApiResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== Validation Errors ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        StringBuilder message = new StringBuilder("Validation failed");
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            message.append(". ").append(error.getField()).append(": ").append(error.getDefaultMessage());
        }
        log.warn("Validation error on {}: {}", request.getRequestURI(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.failure(message.toString(), HttpStatus.BAD_REQUEST.value(), request.getRequestURI()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        log.warn("Constraint violation on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.failure(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), request.getRequestURI()));
    }

    // ==================== 400 Bad Request ====================

    @ExceptionHandler({
        InvalidStoryMediaException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(
            RuntimeException ex, HttpServletRequest request) {
        log.warn("Bad request on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.failure(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), request.getRequestURI()));
    }

    // ==================== 401 Unauthorized ====================

    @ExceptionHandler({
        UnauthorizedStoryActionException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleUnauthorized(
            RuntimeException ex, HttpServletRequest request) {
        log.warn("Unauthorized access attempt on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.failure(ex.getMessage(), HttpStatus.UNAUTHORIZED.value(), request.getRequestURI()));
    }

    // ==================== 403 Forbidden ====================

    @ExceptionHandler({
        StoryAccessDeniedException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleForbidden(
            RuntimeException ex, HttpServletRequest request) {
        log.warn("Access denied on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.failure(ex.getMessage(), HttpStatus.FORBIDDEN.value(), request.getRequestURI()));
    }

    // ==================== 404 Not Found ====================

    @ExceptionHandler({
        StoryNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleNotFound(
            RuntimeException ex, HttpServletRequest request) {
        log.warn("Resource not found on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.failure(ex.getMessage(), HttpStatus.NOT_FOUND.value(), request.getRequestURI()));
    }

    // ==================== 409 Conflict ====================

    @ExceptionHandler({
        DuplicateStoryViewException.class,
        StoryAlreadyDeletedException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleConflict(
            RuntimeException ex, HttpServletRequest request) {
        log.warn("Conflict on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.failure(ex.getMessage(), HttpStatus.CONFLICT.value(), request.getRequestURI()));
    }

    // ==================== 410 Gone ====================

    @ExceptionHandler({
        StoryExpiredException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleGone(
            RuntimeException ex, HttpServletRequest request) {
        log.info("Expired resource accessed on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.GONE)
            .body(ApiResponse.failure(ex.getMessage(), HttpStatus.GONE.value(), request.getRequestURI()));
    }

    // ==================== 503 Service Unavailable ====================

    @ExceptionHandler({
        MediaServiceUnavailableException.class
    })
    public ResponseEntity<ApiResponse<Object>> handleServiceUnavailable(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Service unavailable during request to {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ApiResponse.failure(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value(), request.getRequestURI()));
    }

    // ==================== 500 Internal Server Error ====================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.failure("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI()));
    }
}
