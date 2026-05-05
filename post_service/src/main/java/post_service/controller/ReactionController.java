package post_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import post_service.common.response.DataResponseMessage;
import post_service.common.response.ResponseMessage;
import post_service.dto.request.ReactCommentRequest;
import post_service.dto.request.ReactPostRequest;
import post_service.dto.response.CommentResponse;
import post_service.dto.response.PostResponse;
import post_service.security.AuthUserProvider;
import post_service.service.ReactionService;

@RestController
@RequestMapping("/v1/api/posts")
@RequiredArgsConstructor
@Tag(name = "Reaction", description = "Post and comment reaction endpoints")
public class ReactionController {

    private final ReactionService reactionService;
    private final AuthUserProvider authUserProvider;

    @PostMapping("/{postId}/reactions")
    @Operation(summary = "React to post", description = "Adds or updates a reaction for a post.")
    public ResponseEntity<DataResponseMessage<PostResponse>> reactToPost(
            @PathVariable Long postId,
            @Valid @RequestBody ReactPostRequest request,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(reactionService.reactToPost(authUserId, postId, request));
    }

    @DeleteMapping("/{postId}/reactions")
    @Operation(summary = "Remove post reaction", description = "Removes the authenticated user's reaction from a post.")
    public ResponseEntity<ResponseMessage> removePostReaction(
            @PathVariable Long postId,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(reactionService.removePostReaction(authUserId, postId));
    }

    @PostMapping("/comments/{commentId}/reactions")
    @Operation(summary = "React to comment", description = "Adds or updates a reaction for a comment.")
    public ResponseEntity<DataResponseMessage<CommentResponse>> reactToComment(
            @PathVariable Long commentId,
            @Valid @RequestBody ReactCommentRequest request,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(reactionService.reactToComment(authUserId, commentId, request));
    }

    @DeleteMapping("/comments/{commentId}/reactions")
    @Operation(summary = "Remove comment reaction", description = "Removes the authenticated user's reaction from a comment.")
    public ResponseEntity<ResponseMessage> removeCommentReaction(
            @PathVariable Long commentId,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(reactionService.removeCommentReaction(authUserId, commentId));
    }
}
