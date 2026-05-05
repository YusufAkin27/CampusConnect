package post_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import post_service.common.response.DataResponseMessage;
import post_service.common.response.PageResponse;
import post_service.common.response.ResponseMessage;
import post_service.dto.request.CreateCommentRequest;
import post_service.dto.request.UpdateCommentRequest;
import post_service.dto.response.CommentResponse;
import post_service.security.AuthUserProvider;
import post_service.service.CommentService;

@RestController
@RequestMapping("/v1/api/posts")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "Comment management endpoints")
public class CommentController {

    private final CommentService commentService;
    private final AuthUserProvider authUserProvider;

    @PostMapping("/{postId}/comments")
    @Operation(summary = "Create comment", description = "Creates a comment or reply under a post.")
    public ResponseEntity<DataResponseMessage<CommentResponse>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(authUserId, postId, request));
    }

    @GetMapping("/{postId}/comments")
    @Operation(summary = "Get post comments", description = "Returns paginated top-level comments for a post.")
    public ResponseEntity<DataResponseMessage<PageResponse<CommentResponse>>> getPostComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        Long authUserId = tryGetAuthUserId(httpRequest);
        return ResponseEntity.ok(commentService.getPostComments(authUserId, postId, page, size));
    }

    @PutMapping("/comments/{commentId}")
    @Operation(summary = "Update comment", description = "Updates a comment owned by the authenticated user.")
    public ResponseEntity<DataResponseMessage<CommentResponse>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(commentService.updateComment(authUserId, commentId, request));
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Delete comment", description = "Soft deletes a comment by the owner.")
    public ResponseEntity<ResponseMessage> deleteComment(
            @PathVariable Long commentId,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(commentService.deleteComment(authUserId, commentId));
    }

    @GetMapping("/comments/{commentId}/replies")
    @Operation(summary = "Get comment replies", description = "Returns paginated replies to a comment.")
    public ResponseEntity<DataResponseMessage<PageResponse<CommentResponse>>> getCommentReplies(
            @PathVariable Long commentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        Long authUserId = tryGetAuthUserId(httpRequest);
        return ResponseEntity.ok(commentService.getCommentReplies(authUserId, commentId, page, size));
    }

    private Long tryGetAuthUserId(HttpServletRequest httpRequest) {
        try {
            return authUserProvider.getCurrentAuthUserId(httpRequest);
        } catch (Exception e) {
            return null;
        }
    }
}
