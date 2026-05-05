package post_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import post_service.common.response.DataResponseMessage;
import post_service.dto.response.PostSummaryResponse;
import post_service.entity.Post;
import post_service.enums.PostStatus;
import post_service.mapper.PostMapper;
import post_service.repository.PostRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Internal endpoints for service-to-service communication.
 * Used by: user-service, notification-service, search-service, admin-service.
 *
 * These endpoints do NOT require user authentication - they use service-level trust.
 * TODO: Add API Key or service token validation for production security.
 */
@RestController
@RequestMapping("/v1/api/posts/internal")
@RequiredArgsConstructor
@Tag(name = "Internal", description = "Internal service-to-service endpoints")
public class InternalPostController {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @GetMapping("/user/{authUserId}/count")
    @Operation(summary = "Get user post count", description = "Returns the number of active posts for a user.")
    public ResponseEntity<DataResponseMessage<Long>> getUserPostCount(@PathVariable Long authUserId) {
        Long count = postRepository.countByAuthUserIdAndStatus(authUserId, PostStatus.ACTIVE);
        return ResponseEntity.ok(DataResponseMessage.success("User post count retrieved.", count));
    }

    @GetMapping("/{postId}/summary")
    @Operation(summary = "Get post summary", description = "Returns a lightweight post summary for other services.")
    public ResponseEntity<DataResponseMessage<PostSummaryResponse>> getPostSummary(@PathVariable Long postId) {
        Post post = postRepository.findByIdAndStatusNot(postId, PostStatus.DELETED)
                .orElse(null);
        if (post == null) {
            return ResponseEntity.ok(DataResponseMessage.failure("Post not found.", null));
        }
        return ResponseEntity.ok(DataResponseMessage.success("Post summary retrieved.", postMapper.toPostSummaryResponse(post)));
    }

    @GetMapping("/user/{authUserId}/latest")
    @Operation(summary = "Get user latest posts", description = "Returns the latest active posts for a user.")
    public ResponseEntity<DataResponseMessage<List<PostSummaryResponse>>> getUserLatestPosts(
            @PathVariable Long authUserId,
            @RequestParam(defaultValue = "5") int limit) {
        Page<Post> posts = postRepository.findByAuthUserIdAndStatus(
                authUserId, PostStatus.ACTIVE,
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<PostSummaryResponse> summaries = posts.getContent().stream()
                .map(postMapper::toPostSummaryResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(DataResponseMessage.success("User latest posts retrieved.", summaries));
    }
}
