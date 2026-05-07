package admin_service.exception.handler;

import admin_service.common.response.ErrorResponse;
import admin_service.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the admin-service.
 * Catches all custom and standard exceptions and returns standardized error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ============================================================
    // 404 - Not Found
    // ============================================================

    @ExceptionHandler(AdminNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAdminNotFound(AdminNotFoundException ex, HttpServletRequest request) {
        log.warn("AdminNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(ex.getMessage(), "ADMIN_NOT_FOUND", HttpStatus.NOT_FOUND, request));
    }

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReportNotFound(ReportNotFoundException ex, HttpServletRequest request) {
        log.warn("ReportNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(ex.getMessage(), "REPORT_NOT_FOUND", HttpStatus.NOT_FOUND, request));
    }

    @ExceptionHandler(SupportTicketNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTicketNotFound(SupportTicketNotFoundException ex, HttpServletRequest request) {
        log.warn("SupportTicketNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(ex.getMessage(), "TICKET_NOT_FOUND", HttpStatus.NOT_FOUND, request));
    }

    @ExceptionHandler(UserBanRecordNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBanRecordNotFound(UserBanRecordNotFoundException ex, HttpServletRequest request) {
        log.warn("UserBanRecordNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(ex.getMessage(), "BAN_RECORD_NOT_FOUND", HttpStatus.NOT_FOUND, request));
    }

    // ============================================================
    // 409 - Conflict
    // ============================================================

    @ExceptionHandler(AdminAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAdminAlreadyExists(AdminAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("AdminAlreadyExistsException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(ex.getMessage(), "ADMIN_ALREADY_EXISTS", HttpStatus.CONFLICT, request));
    }

    @ExceptionHandler(UserAlreadyBannedException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyBanned(UserAlreadyBannedException ex, HttpServletRequest request) {
        log.warn("UserAlreadyBannedException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(ex.getMessage(), "USER_ALREADY_BANNED", HttpStatus.CONFLICT, request));
    }

    // ============================================================
    // 400 - Bad Request
    // ============================================================

    @ExceptionHandler(InvalidReportStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidReportStatus(InvalidReportStatusException ex, HttpServletRequest request) {
        log.warn("InvalidReportStatusException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(ex.getMessage(), "INVALID_REPORT_STATUS", HttpStatus.BAD_REQUEST, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.warn("Validation error on request {}: {}", request.getRequestURI(), errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError("Validation failed: " + errors, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST, request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(ex.getMessage(), "BAD_REQUEST", HttpStatus.BAD_REQUEST, request));
    }

    // ============================================================
    // 403 - Forbidden
    // ============================================================

    @ExceptionHandler(UnauthorizedAdminActionException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAction(UnauthorizedAdminActionException ex, HttpServletRequest request) {
        log.warn("UnauthorizedAdminActionException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildError(ex.getMessage(), "UNAUTHORIZED_ACTION", HttpStatus.FORBIDDEN, request));
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ErrorResponse> handlePermissionDenied(PermissionDeniedException ex, HttpServletRequest request) {
        log.warn("PermissionDeniedException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildError(ex.getMessage(), "PERMISSION_DENIED", HttpStatus.FORBIDDEN, request));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("AccessDeniedException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildError("You do not have permission to perform this action.", "ACCESS_DENIED", HttpStatus.FORBIDDEN, request));
    }

    // ============================================================
    // 502 / 503 - External Service Errors
    // ============================================================

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceError(ExternalServiceException ex, HttpServletRequest request) {
        log.error("ExternalServiceException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(buildError(ex.getMessage(), "EXTERNAL_SERVICE_ERROR", HttpStatus.BAD_GATEWAY, request));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(ServiceUnavailableException ex, HttpServletRequest request) {
        log.error("ServiceUnavailableException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(buildError(ex.getMessage(), "SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE, request));
    }

    // ============================================================
    // 500 - Internal Server Error (Fallback)
    // ============================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError("An unexpected error occurred. Please try again later.",
                        "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, request));
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
