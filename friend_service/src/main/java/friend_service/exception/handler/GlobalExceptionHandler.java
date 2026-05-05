package friend_service.exception.handler;

import friend_service.exception.*;
import friend_service.exception.response.ErrorResponse;
import friend_service.exception.response.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
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

/**
 * Global exception handler for friend-service.
 * Translates domain exceptions into structured HTTP error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==========================================
    // Validation Errors (400)
    // ==========================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .success(false)
                .message("Validation failed")
                .validationErrors(errors)
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidFriendRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFriendRequest(
            InvalidFriendRequestException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), "INVALID_FRIEND_REQUEST", request);
    }

    @ExceptionHandler(InvalidFollowException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFollow(
            InvalidFollowException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), "INVALID_FOLLOW", request);
    }

    @ExceptionHandler(InvalidRelationOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRelation(
            InvalidRelationOperationException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), "INVALID_RELATION_OPERATION", request);
    }

    @ExceptionHandler(NotFriendsException.class)
    public ResponseEntity<ErrorResponse> handleNotFriends(
            NotFriendsException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), "NOT_FRIENDS", request);
    }

    // ==========================================
    // Unauthorized (401)
    // ==========================================

    @ExceptionHandler(UnauthorizedUserOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedUserOperationException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), "UNAUTHORIZED", request);
    }

    // ==========================================
    // Forbidden (403)
    // ==========================================

    @ExceptionHandler(FriendRequestAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            FriendRequestAccessDeniedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), "ACCESS_DENIED", request);
    }

    // ==========================================
    // Not Found (404)
    // ==========================================

    @ExceptionHandler(FriendRequestNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFriendRequestNotFound(
            FriendRequestNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), "FRIEND_REQUEST_NOT_FOUND", request);
    }

    @ExceptionHandler(FriendshipNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFriendshipNotFound(
            FriendshipNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), "FRIENDSHIP_NOT_FOUND", request);
    }

    @ExceptionHandler(FollowNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFollowNotFound(
            FollowNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), "FOLLOW_NOT_FOUND", request);
    }

    @ExceptionHandler(SuggestionIgnoreNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSuggestionIgnoreNotFound(
            SuggestionIgnoreNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), "SUGGESTION_IGNORE_NOT_FOUND", request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), "USER_NOT_FOUND", request);
    }

    // ==========================================
    // Conflict (409)
    // ==========================================

    @ExceptionHandler(FriendRequestAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleFriendRequestAlreadyExists(
            FriendRequestAlreadyExistsException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), "FRIEND_REQUEST_ALREADY_EXISTS", request);
    }

    @ExceptionHandler(AlreadyFriendsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyFriends(
            AlreadyFriendsException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), "ALREADY_FRIENDS", request);
    }

    @ExceptionHandler(FollowAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleFollowAlreadyExists(
            FollowAlreadyExistsException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), "FOLLOW_ALREADY_EXISTS", request);
    }

    @ExceptionHandler(SuggestionAlreadyIgnoredException.class)
    public ResponseEntity<ErrorResponse> handleSuggestionAlreadyIgnored(
            SuggestionAlreadyIgnoredException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage(), "SUGGESTION_ALREADY_IGNORED", request);
    }

    // ==========================================
    // Service Unavailable (503)
    // ==========================================

    @ExceptionHandler(UserServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleUserServiceUnavailable(
            UserServiceUnavailableException ex, HttpServletRequest request) {
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), "USER_SERVICE_UNAVAILABLE", request);
    }

    // ==========================================
    // Internal Server Error (500)
    // ==========================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred", "INTERNAL_SERVER_ERROR", request);
    }

    // ==========================================
    // Builder Helper
    // ==========================================

    private ResponseEntity<ErrorResponse> buildError(
            HttpStatus status, String message, String errorCode, HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .status(status.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
