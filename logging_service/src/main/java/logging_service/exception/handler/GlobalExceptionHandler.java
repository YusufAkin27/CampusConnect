package logging_service.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import logging_service.exception.*;
import logging_service.exception.response.ErrorResponse;
import logging_service.exception.response.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .success(false)
                .message("Validation failed")
                .validationErrors(errors)
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(LogNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLogNotFoundException(
            LogNotFoundException ex,
            HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), "LOG_NOT_FOUND", HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(ErrorLogNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleErrorLogNotFoundException(
            ErrorLogNotFoundException ex,
            HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), "ERROR_LOG_NOT_FOUND", HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(RetentionPolicyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRetentionPolicyNotFoundException(
            RetentionPolicyNotFoundException ex,
            HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), "RETENTION_POLICY_NOT_FOUND", HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(BatchLogSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleBatchLogSizeExceededException(
            BatchLogSizeExceededException ex,
            HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), "BATCH_SIZE_EXCEEDED", HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InvalidLogRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidLogRequestException(
            InvalidLogRequestException ex,
            HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), "INVALID_LOG_REQUEST", HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InvalidRetentionPolicyException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRetentionPolicyException(
            InvalidRetentionPolicyException ex,
            HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), "INVALID_RETENTION_POLICY", HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(UnauthorizedLogAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedLogAccessException(
            UnauthorizedLogAccessException ex,
            HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), "UNAUTHORIZED_ACCESS", HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return buildErrorResponse(
                "An unexpected error occurred. Please try again later.",
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String message,
            String errorCode,
            HttpStatus status,
            HttpServletRequest request) {

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .status(status.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
