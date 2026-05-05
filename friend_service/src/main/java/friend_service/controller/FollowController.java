package friend_service.controller;

import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.common.response.ResponseMessage;
import friend_service.dto.response.FollowResponse;
import friend_service.dto.response.FollowerResponse;
import friend_service.dto.response.FollowingResponse;
import friend_service.security.AuthUserProvider;
import friend_service.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for follow/unfollow and follower/following list operations.
 * Base path: /v1/api/friends/follows
 */
@RestController
@RequestMapping("/v1/api/friends/follows")
@RequiredArgsConstructor
@Tag(name = "Follow", description = "APIs for managing follow/unfollow relationships")
public class FollowController {

    private final FollowService followService;
    private final AuthUserProvider authUserProvider;

    @Operation(summary = "Follow a user",
            description = "Follows a target user. Reactivates an existing UNFOLLOWED record if present.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User followed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid follow (e.g., self-follow)")
    })
    @PostMapping("/{targetAuthUserId}")
    public ResponseEntity<DataResponseMessage<FollowResponse>> followUser(
            @PathVariable Long targetAuthUserId,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(followService.followUser(authUserId, targetAuthUserId));
    }

    @Operation(summary = "Unfollow a user",
            description = "Unfollows a target user. Soft-deletes the follow record (status = UNFOLLOWED).")
    @DeleteMapping("/{targetAuthUserId}")
    public ResponseEntity<ResponseMessage> unfollowUser(
            @PathVariable Long targetAuthUserId,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(followService.unfollowUser(authUserId, targetAuthUserId));
    }

    @Operation(summary = "Get followers of a user",
            description = "Returns a paginated list of users who follow the target user.")
    @GetMapping("/followers/{targetAuthUserId}")
    public ResponseEntity<DataResponseMessage<PageResponse<FollowerResponse>>> getFollowers(
            @PathVariable Long targetAuthUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        authUserProvider.getCurrentAuthUserId(httpRequest); // Auth check only
        return ResponseEntity.ok(followService.getFollowers(targetAuthUserId, page, size));
    }

    @Operation(summary = "Get users followed by a user",
            description = "Returns a paginated list of users that the target user follows.")
    @GetMapping("/following/{targetAuthUserId}")
    public ResponseEntity<DataResponseMessage<PageResponse<FollowingResponse>>> getFollowing(
            @PathVariable Long targetAuthUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        authUserProvider.getCurrentAuthUserId(httpRequest); // Auth check only
        return ResponseEntity.ok(followService.getFollowing(targetAuthUserId, page, size));
    }

    @Operation(summary = "Check follow status",
            description = "Returns true if the authenticated user is currently following the target user.")
    @GetMapping("/check/{targetAuthUserId}")
    public ResponseEntity<DataResponseMessage<Boolean>> isFollowing(
            @PathVariable Long targetAuthUserId,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(followService.isFollowing(authUserId, targetAuthUserId));
    }
}
