package friend_service.controller;

import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.common.response.ResponseMessage;
import friend_service.dto.response.FriendshipResponse;
import friend_service.dto.response.MutualFriendResponse;
import friend_service.security.AuthUserProvider;
import friend_service.service.FriendshipService;
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
 * Controller for friendship management operations.
 * Base path: /v1/api/friends
 */
@RestController
@RequestMapping("/v1/api/friends")
@RequiredArgsConstructor
@Tag(name = "Friendship", description = "APIs for managing confirmed friendships")
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final AuthUserProvider authUserProvider;

    @Operation(summary = "Get my friends",
            description = "Returns a paginated list of the authenticated user's active friends.")
    @GetMapping("/me")
    public ResponseEntity<DataResponseMessage<PageResponse<FriendshipResponse>>> getMyFriends(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(friendshipService.getMyFriends(authUserId, page, size));
    }

    @Operation(summary = "Get another user's friends",
            description = "Returns a paginated list of a target user's active friends (public view).")
    @GetMapping("/user/{targetAuthUserId}")
    public ResponseEntity<DataResponseMessage<PageResponse<FriendshipResponse>>> getUserFriends(
            @PathVariable Long targetAuthUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(friendshipService.getUserFriends(authUserId, targetAuthUserId, page, size));
    }

    @Operation(summary = "Remove a friend",
            description = "Soft-deletes the friendship between the authenticated user and a friend. Follow relationships remain unaffected.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friend removed successfully"),
            @ApiResponse(responseCode = "400", description = "Not friends with this user"),
            @ApiResponse(responseCode = "404", description = "Friendship not found")
    })
    @DeleteMapping("/{friendAuthUserId}")
    public ResponseEntity<ResponseMessage> removeFriend(
            @PathVariable Long friendAuthUserId,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(friendshipService.removeFriend(authUserId, friendAuthUserId));
    }

    @Operation(summary = "Check if friends",
            description = "Returns true if the authenticated user and the target are currently friends.")
    @GetMapping("/check/{targetAuthUserId}")
    public ResponseEntity<DataResponseMessage<Boolean>> areFriends(
            @PathVariable Long targetAuthUserId,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(friendshipService.areFriends(authUserId, targetAuthUserId));
    }

    @Operation(summary = "Get mutual friends",
            description = "Returns a paginated list of mutual friends between the authenticated user and the target.")
    @GetMapping("/mutual/{targetAuthUserId}")
    public ResponseEntity<DataResponseMessage<PageResponse<MutualFriendResponse>>> getMutualFriends(
            @PathVariable Long targetAuthUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(friendshipService.getMutualFriends(authUserId, targetAuthUserId, page, size));
    }

    @Operation(summary = "Get mutual friend count",
            description = "Returns the number of mutual friends between the authenticated user and the target.")
    @GetMapping("/mutual/{targetAuthUserId}/count")
    public ResponseEntity<DataResponseMessage<Long>> getMutualFriendCount(
            @PathVariable Long targetAuthUserId,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(friendshipService.getMutualFriendCount(authUserId, targetAuthUserId));
    }
}
