package friend_service.controller;

import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.common.response.ResponseMessage;
import friend_service.dto.request.SendFriendRequestRequest;
import friend_service.dto.response.FriendRequestResponse;
import friend_service.security.AuthUserProvider;
import friend_service.service.FriendRequestService;
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

/**
 * Controller for friend request lifecycle operations.
 * Base path: /v1/api/friends/requests
 */
@RestController
@RequestMapping("/v1/api/friends/requests")
@RequiredArgsConstructor
@Tag(name = "Friend Requests", description = "APIs for managing friend request lifecycle")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;
    private final AuthUserProvider authUserProvider;

    @Operation(summary = "Send a friend request",
            description = "Sends a friend request to another user. Handles reverse-pending auto-accept.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Friend request sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request (e.g., self-request)"),
            @ApiResponse(responseCode = "409", description = "Friend request already exists or already friends")
    })
    @PostMapping
    public ResponseEntity<DataResponseMessage<FriendRequestResponse>> sendFriendRequest(
            @Valid @RequestBody SendFriendRequestRequest request,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(friendRequestService.sendFriendRequest(authUserId, request));
    }

    @Operation(summary = "Get received friend requests",
            description = "Returns a paginated list of pending friend requests received by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Received requests retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/received")
    public ResponseEntity<DataResponseMessage<PageResponse<FriendRequestResponse>>> getReceivedRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(friendRequestService.getReceivedRequests(authUserId, page, size));
    }

    @Operation(summary = "Get sent friend requests",
            description = "Returns a paginated list of pending friend requests sent by the authenticated user.")
    @GetMapping("/sent")
    public ResponseEntity<DataResponseMessage<PageResponse<FriendRequestResponse>>> getSentRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(friendRequestService.getSentRequests(authUserId, page, size));
    }

    @Operation(summary = "Accept a friend request",
            description = "Accepts a pending friend request. Only the receiver can accept. Creates a friendship.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friend request accepted"),
            @ApiResponse(responseCode = "403", description = "Access denied - not the receiver"),
            @ApiResponse(responseCode = "404", description = "Friend request not found")
    })
    @PatchMapping("/{requestId}/accept")
    public ResponseEntity<DataResponseMessage<FriendRequestResponse>> acceptFriendRequest(
            @PathVariable Long requestId,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(friendRequestService.acceptFriendRequest(authUserId, requestId));
    }

    @Operation(summary = "Reject a friend request",
            description = "Rejects a pending friend request. Only the receiver can reject.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friend request rejected"),
            @ApiResponse(responseCode = "403", description = "Access denied - not the receiver"),
            @ApiResponse(responseCode = "404", description = "Friend request not found")
    })
    @PatchMapping("/{requestId}/reject")
    public ResponseEntity<DataResponseMessage<FriendRequestResponse>> rejectFriendRequest(
            @PathVariable Long requestId,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(friendRequestService.rejectFriendRequest(authUserId, requestId));
    }

    @Operation(summary = "Cancel a sent friend request",
            description = "Cancels a pending friend request previously sent by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Friend request cancelled"),
            @ApiResponse(responseCode = "403", description = "Access denied - not the sender"),
            @ApiResponse(responseCode = "404", description = "Friend request not found")
    })
    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ResponseMessage> cancelFriendRequest(
            @PathVariable Long requestId,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(friendRequestService.cancelFriendRequest(authUserId, requestId));
    }
}
