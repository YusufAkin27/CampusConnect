package post_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import post_service.dto.request.CreatePostRequest;
import post_service.dto.request.UpdatePostRequest;
import post_service.dto.request.UpdatePostStatusRequest;
import post_service.dto.response.PostDetailResponse;
import post_service.dto.response.PostResponse;
import post_service.dto.response.PostStatsResponse;
import post_service.enums.SortType;
import post_service.security.AuthUserProvider;
import post_service.service.PostService;

@RestController
@RequestMapping("/v1/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post", description = "Post management endpoints")
public class PostController {

    private final PostService postService;
    private final AuthUserProvider authUserProvider;

    @PostMapping
    @Operation(summary = "Create post", description = "Creates a new post for the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Post created"),
            @ApiResponse(responseCode = "400", description = "Invalid post data")
    })
    public ResponseEntity<DataResponseMessage<PostResponse>> createPost(
            @Valid @RequestBody CreatePostRequest request,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(authUserId, request));
    }

    @PutMapping("/{postId}")
    @Operation(summary = "Update post", description = "Updates a post owned by the authenticated user.")
    public ResponseEntity<DataResponseMessage<PostResponse>> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(postService.updatePost(authUserId, postId, request));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete post", description = "Soft deletes a post owned by the authenticated user.")
    public ResponseEntity<ResponseMessage> deletePost(
            @PathVariable Long postId,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(postService.deletePost(authUserId, postId));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Get post detail", description = "Returns the full detail of a post.")
    public ResponseEntity<DataResponseMessage<PostDetailResponse>> getPostDetail(
            @PathVariable Long postId,
            HttpServletRequest httpRequest) {
        Long authUserId = tryGetAuthUserId(httpRequest);
        return ResponseEntity.ok(postService.getPostDetail(authUserId, postId));
    }

    @GetMapping("/feed")
    @Operation(summary = "Get feed", description = "Returns paginated feed posts.")
    public ResponseEntity<DataResponseMessage<PageResponse<PostResponse>>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "NEWEST") SortType sortType,
            HttpServletRequest httpRequest) {
        Long authUserId = tryGetAuthUserId(httpRequest);
        return ResponseEntity.ok(postService.getFeed(authUserId, page, size, sortType));
    }

    @GetMapping("/me")
    @Operation(summary = "Get my posts", description = "Returns the authenticated user's posts.")
    public ResponseEntity<DataResponseMessage<PageResponse<PostResponse>>> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(postService.getMyPosts(authUserId, page, size));
    }

    @GetMapping("/user/{targetAuthUserId}")
    @Operation(summary = "Get user public posts", description = "Returns a specific user's public posts.")
    public ResponseEntity<DataResponseMessage<PageResponse<PostResponse>>> getUserPublicPosts(
            @PathVariable Long targetAuthUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        Long requesterAuthUserId = tryGetAuthUserId(httpRequest);
        return ResponseEntity.ok(postService.getUserPublicPosts(requesterAuthUserId, targetAuthUserId, page, size));
    }

    @GetMapping("/search")
    @Operation(summary = "Search posts", description = "Searches public active posts by keyword.")
    public ResponseEntity<DataResponseMessage<PageResponse<PostResponse>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        Long authUserId = tryGetAuthUserId(httpRequest);
        return ResponseEntity.ok(postService.searchPosts(authUserId, keyword, page, size));
    }

    @GetMapping("/{postId}/stats")
    @Operation(summary = "Get post stats", description = "Returns engagement statistics for a post.")
    public ResponseEntity<DataResponseMessage<PostStatsResponse>> getPostStats(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostStats(postId));
    }

    @PatchMapping("/{postId}/archive")
    @Operation(summary = "Archive post", description = "Archives a post owned by the authenticated user.")
    public ResponseEntity<ResponseMessage> archivePost(
            @PathVariable Long postId,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(postService.archivePost(authUserId, postId));
    }

    @PatchMapping("/{postId}/pin")
    @Operation(summary = "Pin post", description = "Pins a post owned by the authenticated user.")
    public ResponseEntity<ResponseMessage> pinPost(
            @PathVariable Long postId,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(postService.pinPost(authUserId, postId));
    }

    @PatchMapping("/{postId}/unpin")
    @Operation(summary = "Unpin post", description = "Removes the pin from a post owned by the authenticated user.")
    public ResponseEntity<ResponseMessage> unpinPost(
            @PathVariable Long postId,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(postService.unpinPost(authUserId, postId));
    }

    @PatchMapping("/admin/{postId}/status")
    @Operation(summary = "Update post status (Admin)", description = "Admin endpoint to update post status.")
    public ResponseEntity<DataResponseMessage<PostResponse>> updatePostStatus(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostStatusRequest request) {
        return ResponseEntity.ok(postService.updatePostStatus(postId, request));
    }

    /**
     * Attempts to read authUserId without throwing an exception if header is missing.
     */
    private Long tryGetAuthUserId(HttpServletRequest httpRequest) {
        try {
            return authUserProvider.getCurrentAuthUserId(httpRequest);
        } catch (Exception e) {
            return null;
        }
    }
}
