package user_service.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import user_service.exception.*;
import user_service.exception.response.ErrorResponse;
import user_service.exception.response.ValidationErrorResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the user-service.
 * Catches all custom and standard exceptions and returns standardized error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ============================================================
    // 404 - Not Found
    // ============================================================

    @ExceptionHandler(UserProfileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserProfileNotFound(
            UserProfileNotFoundException ex,
            HttpServletRequest request) {
        log.warn("UserProfileNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(ex.getMessage(), "USER_PROFILE_NOT_FOUND", HttpStatus.NOT_FOUND, request));
    }

    // ============================================================
    // 409 - Conflict
    // ============================================================

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUsername(
            DuplicateUsernameException ex,
            HttpServletRequest request) {
        log.warn("DuplicateUsernameException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(ex.getMessage(), "DUPLICATE_USERNAME", HttpStatus.CONFLICT, request));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(
            DuplicateEmailException ex,
            HttpServletRequest request) {
        log.warn("DuplicateEmailException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(ex.getMessage(), "DUPLICATE_EMAIL", HttpStatus.CONFLICT, request));
    }

    @ExceptionHandler(DuplicateStudentNumberException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateStudentNumber(
            DuplicateStudentNumberException ex,
            HttpServletRequest request) {
        log.warn("DuplicateStudentNumberException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(ex.getMessage(), "DUPLICATE_STUDENT_NUMBER", HttpStatus.CONFLICT, request));
    }

    @ExceptionHandler(UserProfileAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserProfileAlreadyExists(
            UserProfileAlreadyExistsException ex,
            HttpServletRequest request) {
        log.warn("UserProfileAlreadyExistsException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(ex.getMessage(), "USER_PROFILE_ALREADY_EXISTS", HttpStatus.CONFLICT, request));
    }

    // ============================================================
    // 400 - Bad Request
    // ============================================================

    @ExceptionHandler(InvalidProfileDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProfileData(
            InvalidProfileDataException ex,
            HttpServletRequest request) {
        log.warn("InvalidProfileDataException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(ex.getMessage(), "INVALID_PROFILE_DATA", HttpStatus.BAD_REQUEST, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.warn("Validation error on request {}: {}", request.getRequestURI(), errors);
        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .success(false)
                .message("Validation failed. Please check the provided data.")
                .validationErrors(errors)
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ============================================================
    // 403 - Forbidden
    // ============================================================

    @ExceptionHandler(PrivateProfileException.class)
    public ResponseEntity<ErrorResponse> handlePrivateProfile(
            PrivateProfileException ex,
            HttpServletRequest request) {
        log.warn("PrivateProfileException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildError(ex.getMessage(), "PRIVATE_PROFILE", HttpStatus.FORBIDDEN, request));
    }

    @ExceptionHandler(InactiveUserException.class)
    public ResponseEntity<ErrorResponse> handleInactiveUser(
            InactiveUserException ex,
            HttpServletRequest request) {
        log.warn("InactiveUserException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildError(ex.getMessage(), "INACTIVE_USER", HttpStatus.FORBIDDEN, request));
    }

    @ExceptionHandler(UnauthorizedUserOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedOperation(
            UnauthorizedUserOperationException ex,
            HttpServletRequest request) {
        log.warn("UnauthorizedUserOperationException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildError(ex.getMessage(), "UNAUTHORIZED_OPERATION", HttpStatus.FORBIDDEN, request));
    }

    // ============================================================
    // 500 - Internal Server Error
    // ============================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        "An unexpected error occurred. Please try again later.",
                        "INTERNAL_SERVER_ERROR",
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        request
                ));
    }

    // ============================================================
    // Helper
    // ============================================================

    private ErrorResponse buildError(String message, String errorCode, HttpStatus status, HttpServletRequest request) {
        return ErrorResponse.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .status(status.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
