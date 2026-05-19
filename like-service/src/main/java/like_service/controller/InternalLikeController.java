package like_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import like_service.dto.request.BulkLikeCountRequest;
import like_service.dto.response.BulkLikeCountResponse;
import like_service.dto.response.LikeCountResponse;
import like_service.entity.LikeTargetType;
import like_service.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Internal Like Controller.
 * Servisler arası iletişim için kullanılır.
 * API Gateway üzerinden dış kullanıcıya açılmamalıdır.
 */
@Slf4j
@RestController
@RequestMapping("/internal/likes")
@RequiredArgsConstructor
@Tag(name = "Internal Like API", description = "Servisler arası like count ve like temizleme işlemleri")
public class InternalLikeController {

    private final LikeService likeService;

    @GetMapping("/count")
    @Operation(summary = "Like sayısı getir (Internal)", description = "Belirtilen içeriğin aktif beğeni sayısını döner.")
    public ResponseEntity<LikeCountResponse> getLikeCount(
            @Parameter(description = "Hedef içerik ID") @RequestParam UUID targetId,
            @Parameter(description = "Hedef içerik türü") @RequestParam LikeTargetType targetType) {
        LikeCountResponse response = likeService.getLikeCount(targetId, targetType);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/count/bulk")
    @Operation(summary = "Bulk like count getir (Internal)", description = "Birden fazla içeriğin beğeni sayısını tek istekle döner.")
    public ResponseEntity<BulkLikeCountResponse> getBulkLikeCounts(@Valid @RequestBody BulkLikeCountRequest request) {
        BulkLikeCountResponse response = likeService.getBulkLikeCounts(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "Post beğenilerini pasifleştir", description = "Bir post silindiğinde ona ait tüm beğenileri REMOVED yapar.")
    public ResponseEntity<Void> removePostLikes(@PathVariable UUID postId) {
        log.info("Post beğenileri pasifleştiriliyor: postId={}", postId);
        likeService.removeLikesByTarget(postId, LikeTargetType.POST);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Comment beğenilerini pasifleştir", description = "Bir comment silindiğinde ona ait tüm beğenileri REMOVED yapar.")
    public ResponseEntity<Void> removeCommentLikes(@PathVariable UUID commentId) {
        log.info("Comment beğenileri pasifleştiriliyor: commentId={}", commentId);
        likeService.removeLikesByTarget(commentId, LikeTargetType.COMMENT);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/stories/{storyId}")
    @Operation(summary = "Story beğenilerini pasifleştir", description = "Bir story silindiğinde ona ait tüm beğenileri REMOVED yapar.")
    public ResponseEntity<Void> removeStoryLikes(@PathVariable UUID storyId) {
        log.info("Story beğenileri pasifleştiriliyor: storyId={}", storyId);
        likeService.removeLikesByTarget(storyId, LikeTargetType.STORY);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/events/{eventId}")
    @Operation(summary = "Event beğenilerini pasifleştir", description = "Bir event silindiğinde ona ait tüm beğenileri REMOVED yapar.")
    public ResponseEntity<Void> removeEventLikes(@PathVariable UUID eventId) {
        log.info("Event beğenileri pasifleştiriliyor: eventId={}", eventId);
        likeService.removeLikesByTarget(eventId, LikeTargetType.EVENT);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/media/{mediaId}")
    @Operation(summary = "Media beğenilerini pasifleştir", description = "Bir media silindiğinde ona ait tüm beğenileri REMOVED yapar.")
    public ResponseEntity<Void> removeMediaLikes(@PathVariable UUID mediaId) {
        log.info("Media beğenileri pasifleştiriliyor: mediaId={}", mediaId);
        likeService.removeLikesByTarget(mediaId, LikeTargetType.MEDIA);
        return ResponseEntity.noContent().build();
    }
}
