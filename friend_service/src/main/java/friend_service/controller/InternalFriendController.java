package friend_service.controller;

import friend_service.common.response.DataResponseMessage;
import friend_service.dto.response.InternalFriendStatusResponse;
import friend_service.dto.response.SocialStatsResponse;
import friend_service.repository.FollowRepository;
import friend_service.repository.FriendshipRepository;
import friend_service.service.SocialGraphService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Internal controller providing endpoints for inter-service communication.
 * Base path: /v1/api/friends/internal
 *
 * Consumers:
 * - post-service (feed, visibility filtering)
 * - notification-service (social event decisions)
 * - user-service (profile social stats)
 * - recommendation-service
 *
 * TODO (Production): Require service-to-service authentication (e.g., API key or mTLS)
 *   - .requestMatchers("/v1/api/friends/internal/**").hasRole("SERVICE")
 */
@RestController
@RequestMapping("/v1/api/friends/internal")
@RequiredArgsConstructor
@Tag(name = "Internal", description = "Internal endpoints for inter-service communication only")
public class InternalFriendController {

    private final SocialGraphService socialGraphService;
    private final FriendshipRepository friendshipRepository;
    private final FollowRepository followRepository;

    @Operation(summary = "Get friend/follow status between two users",
            description = "Lightweight status check for inter-service use.")
    @GetMapping("/status")
    public ResponseEntity<DataResponseMessage<InternalFriendStatusResponse>> getInternalFriendStatus(
            @RequestParam Long requesterAuthUserId,
            @RequestParam Long targetAuthUserId
    ) {
        return ResponseEntity.ok(
                socialGraphService.getInternalFriendStatus(requesterAuthUserId, targetAuthUserId));
    }

    @Operation(summary = "Get social stats for a user (internal)")
    @GetMapping("/{authUserId}/stats")
    public ResponseEntity<DataResponseMessage<SocialStatsResponse>> getInternalSocialStats(
            @PathVariable Long authUserId
    ) {
        return ResponseEntity.ok(socialGraphService.getSocialStats(authUserId));
    }

    @Operation(summary = "Get friend authUserId list (internal)",
            description = "Returns all authUserIds that the given user is actively friends with.")
    @GetMapping("/{authUserId}/friend-ids")
    public ResponseEntity<DataResponseMessage<List<Long>>> getFriendIds(
            @PathVariable Long authUserId
    ) {
        List<Long> friendIds = friendshipRepository.getFriendIds(authUserId);
        return ResponseEntity.ok(DataResponseMessage.success("Friend IDs retrieved.", friendIds));
    }

    @Operation(summary = "Get following authUserId list (internal)",
            description = "Returns all authUserIds that the given user is actively following.")
    @GetMapping("/{authUserId}/following-ids")
    public ResponseEntity<DataResponseMessage<List<Long>>> getFollowingIds(
            @PathVariable Long authUserId
    ) {
        List<Long> followingIds = followRepository.findFollowingIds(authUserId);
        return ResponseEntity.ok(DataResponseMessage.success("Following IDs retrieved.", followingIds));
    }
}
