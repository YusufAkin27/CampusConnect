package friend_service.service;

import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.common.response.ResponseMessage;
import friend_service.dto.request.SendFriendRequestRequest;
import friend_service.dto.response.FriendRequestResponse;

/**
 * Service interface for managing friend request lifecycle operations.
 */
public interface FriendRequestService {

    /**
     * Sends a friend request from one user to another.
     * Handles reverse-pending logic (if target already sent a request, auto-accept).
     */
    DataResponseMessage<FriendRequestResponse> sendFriendRequest(
            Long senderAuthUserId,
            SendFriendRequestRequest request
    );

    /**
     * Accepts a pending friend request. Only the receiver can accept.
     * Creates a Friendship record upon acceptance.
     */
    DataResponseMessage<FriendRequestResponse> acceptFriendRequest(
            Long receiverAuthUserId,
            Long requestId
    );

    /**
     * Rejects a pending friend request. Only the receiver can reject.
     */
    DataResponseMessage<FriendRequestResponse> rejectFriendRequest(
            Long receiverAuthUserId,
            Long requestId
    );

    /**
     * Cancels a pending friend request. Only the original sender can cancel.
     */
    ResponseMessage cancelFriendRequest(
            Long senderAuthUserId,
            Long requestId
    );

    /**
     * Returns a paginated list of pending friend requests received by the user.
     */
    DataResponseMessage<PageResponse<FriendRequestResponse>> getReceivedRequests(
            Long receiverAuthUserId,
            int page,
            int size
    );

    /**
     * Returns a paginated list of pending friend requests sent by the user.
     */
    DataResponseMessage<PageResponse<FriendRequestResponse>> getSentRequests(
            Long senderAuthUserId,
            int page,
            int size
    );
}
