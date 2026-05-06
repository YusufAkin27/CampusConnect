package media_service.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import media_service.exception.*;
import media_service.exception.response.ErrorResponse;
import media_service.exception.response.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =================== 400 BAD REQUEST ===================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
            errors.put(field, error.getDefaultMessage());
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

    @ExceptionHandler(InvalidMediaFileException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMediaFile(
            InvalidMediaFileException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "INVALID_MEDIA_FILE", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidMediaUsageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidMediaUsage(
            InvalidMediaUsageException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "INVALID_MEDIA_USAGE", ex.getMessage(), request);
    }

    @ExceptionHandler(MediaValidationException.class)
    public ResponseEntity<ErrorResponse> handleMediaValidation(
            MediaValidationException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "MEDIA_VALIDATION_FAILED", ex.getMessage(), request);
    }

    @ExceptionHandler(BatchMediaValidationException.class)
    public ResponseEntity<ErrorResponse> handleBatchMediaValidation(
            BatchMediaValidationException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "BATCH_MEDIA_VALIDATION_FAILED", ex.getMessage(), request);
    }

    // =================== 401 UNAUTHORIZED ===================

    @ExceptionHandler(UnauthorizedUserOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedUser(
            UnauthorizedUserOperationException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage(), request);
    }

    // =================== 403 FORBIDDEN ===================

    @ExceptionHandler(UnauthorizedMediaAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedMediaAccess(
            UnauthorizedMediaAccessException ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, "FORBIDDEN_MEDIA_ACCESS", ex.getMessage(), request);
    }

    // =================== 404 NOT FOUND ===================

    @ExceptionHandler(MediaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMediaNotFound(
            MediaNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "MEDIA_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(MediaUsageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMediaUsageNotFound(
            MediaUsageNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "MEDIA_USAGE_NOT_FOUND", ex.getMessage(), request);
    }

    // =================== 413 PAYLOAD TOO LARGE ===================

    @ExceptionHandler(MediaSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMediaSizeExceeded(
            MediaSizeExceededException ex, HttpServletRequest request) {
        return buildError(HttpStatus.PAYLOAD_TOO_LARGE, "MEDIA_SIZE_EXCEEDED", ex.getMessage(), request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return buildError(HttpStatus.PAYLOAD_TOO_LARGE, "MAX_UPLOAD_SIZE_EXCEEDED",
                "The uploaded file exceeds the maximum allowed size.", request);
    }

    // =================== 415 UNSUPPORTED MEDIA TYPE ===================

    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
            UnsupportedMediaTypeException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", ex.getMessage(), request);
    }

    // =================== 500 INTERNAL SERVER ERROR ===================

    @ExceptionHandler(MediaUploadException.class)
    public ResponseEntity<ErrorResponse> handleMediaUpload(
            MediaUploadException ex, HttpServletRequest request) {
        log.error("Media upload error: {}", ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "MEDIA_UPLOAD_ERROR", ex.getMessage(), request);
    }

    @ExceptionHandler(MediaStorageException.class)
    public ResponseEntity<ErrorResponse> handleMediaStorage(
            MediaStorageException ex, HttpServletRequest request) {
        log.error("Media storage error: {}", ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "MEDIA_STORAGE_ERROR", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.", request);
    }

    // =================== Helper ===================

    private ResponseEntity<ErrorResponse> buildError(
            HttpStatus status, String errorCode, String message, HttpServletRequest request) {
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
