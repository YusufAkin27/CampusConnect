package post_service.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import post_service.exception.*;
import post_service.exception.response.ErrorResponse;
import post_service.exception.response.ValidationErrorResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== Validation ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
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

    // ==================== Not Found (404) ====================

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFound(PostNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "POST_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFound(CommentNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(HashtagNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHashtagNotFound(HashtagNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "HASHTAG_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReportNotFound(ReportNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "REPORT_NOT_FOUND", ex.getMessage(), request);
    }

    // ==================== Unauthorized (401) ====================

    @ExceptionHandler(UnauthorizedUserOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedUserOperationException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage(), request);
    }

    // ==================== Forbidden (403) ====================

    @ExceptionHandler(PostAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handlePostAccessDenied(PostAccessDeniedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, "POST_ACCESS_DENIED", ex.getMessage(), request);
    }

    @ExceptionHandler(CommentAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleCommentAccessDenied(CommentAccessDeniedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, "COMMENT_ACCESS_DENIED", ex.getMessage(), request);
    }

    // ==================== Conflict (409) ====================

    @ExceptionHandler(PostAlreadyLikedException.class)
    public ResponseEntity<ErrorResponse> handlePostAlreadyLiked(PostAlreadyLikedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, "POST_ALREADY_LIKED", ex.getMessage(), request);
    }

    @ExceptionHandler(CommentAlreadyLikedException.class)
    public ResponseEntity<ErrorResponse> handleCommentAlreadyLiked(CommentAlreadyLikedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, "COMMENT_ALREADY_LIKED", ex.getMessage(), request);
    }

    @ExceptionHandler(PostAlreadySavedException.class)
    public ResponseEntity<ErrorResponse> handlePostAlreadySaved(PostAlreadySavedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, "POST_ALREADY_SAVED", ex.getMessage(), request);
    }

    @ExceptionHandler(PostAlreadyReportedException.class)
    public ResponseEntity<ErrorResponse> handlePostAlreadyReported(PostAlreadyReportedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, "POST_ALREADY_REPORTED", ex.getMessage(), request);
    }

    @ExceptionHandler(CommentAlreadyReportedException.class)
    public ResponseEntity<ErrorResponse> handleCommentAlreadyReported(CommentAlreadyReportedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, "COMMENT_ALREADY_REPORTED", ex.getMessage(), request);
    }

    // ==================== Bad Request (400) ====================

    @ExceptionHandler(InvalidPostDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPostData(InvalidPostDataException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "INVALID_POST_DATA", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidCommentDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCommentData(InvalidCommentDataException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "INVALID_COMMENT_DATA", ex.getMessage(), request);
    }

    @ExceptionHandler(InactivePostException.class)
    public ResponseEntity<ErrorResponse> handleInactivePost(InactivePostException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "INACTIVE_POST", ex.getMessage(), request);
    }

    @ExceptionHandler(InactiveCommentException.class)
    public ResponseEntity<ErrorResponse> handleInactiveComment(InactiveCommentException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "INACTIVE_COMMENT", ex.getMessage(), request);
    }

    @ExceptionHandler(CommentsDisabledException.class)
    public ResponseEntity<ErrorResponse> handleCommentsDisabled(CommentsDisabledException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "COMMENTS_DISABLED", ex.getMessage(), request);
    }

    @ExceptionHandler(LikesDisabledException.class)
    public ResponseEntity<ErrorResponse> handleLikesDisabled(LikesDisabledException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "LIKES_DISABLED", ex.getMessage(), request);
    }

    @ExceptionHandler(PostNotLikedException.class)
    public ResponseEntity<ErrorResponse> handlePostNotLiked(PostNotLikedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "POST_NOT_LIKED", ex.getMessage(), request);
    }

    @ExceptionHandler(CommentNotLikedException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotLiked(CommentNotLikedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "COMMENT_NOT_LIKED", ex.getMessage(), request);
    }

    @ExceptionHandler(PostNotSavedException.class)
    public ResponseEntity<ErrorResponse> handlePostNotSaved(PostNotSavedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "POST_NOT_SAVED", ex.getMessage(), request);
    }

    // ==================== Service Unavailable (503) ====================

    @ExceptionHandler(UserServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleUserServiceUnavailable(UserServiceUnavailableException ex, HttpServletRequest request) {
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, "USER_SERVICE_UNAVAILABLE", ex.getMessage(), request);
    }

    // ==================== Internal Server Error (500) ====================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred.", request);
    }

    // ==================== Helper ====================

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String errorCode, String message, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .status(status.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }
}
