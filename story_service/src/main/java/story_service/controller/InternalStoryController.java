package story_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import story_service.dto.response.ApiResponse;
import story_service.enums.StoryStatus;
import story_service.repository.StoryRepository;

import java.util.UUID;

/**
 * Internal endpoints for service-to-service communication.
 * Used by: like-service, comment-service, admin-service.
 *
 * These endpoints do NOT require user authentication - they use service-level trust.
 */
@RestController
@RequestMapping("/v1/api/stories/internal")
@RequiredArgsConstructor
@Tag(name = "Internal", description = "Internal service-to-service endpoints")
public class InternalStoryController {

    private final StoryRepository storyRepository;

    @GetMapping("/{storyId}/exists")
    @Operation(summary = "Check story exists", description = "Returns true if the story exists and is not deleted. Used by like-service and comment-service.")
    public ResponseEntity<ApiResponse<Boolean>> storyExists(@PathVariable UUID storyId) {
        boolean exists = storyRepository.findById(storyId)
                .map(story -> story.getStatus() != StoryStatus.DELETED && story.getDeletedAt() == null)
                .orElse(false);
        return ResponseEntity.ok(ApiResponse.success("Story existence checked.", exists));
    }
}
