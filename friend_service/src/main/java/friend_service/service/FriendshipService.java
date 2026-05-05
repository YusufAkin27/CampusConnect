package friend_service.service;

import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.common.response.ResponseMessage;
import friend_service.dto.response.FriendshipResponse;
import friend_service.dto.response.MutualFriendResponse;

/**
 * Service interface for managing confirmed friendship operations.
 */
public interface FriendshipService {

    /**
     * Soft-deletes a friendship (sets status = REMOVED).
     * Both parties lose friend status. Follow relationships remain unaffected.
     */
    ResponseMessage removeFriend(Long authUserId, Long friendAuthUserId);

    /**
     * Returns a paginated list of the authenticated user's active friends.
     */
    DataResponseMessage<PageResponse<FriendshipResponse>> getMyFriends(
            Long authUserId,
            int page,
            int size
    );

    /**
     * Returns a paginated list of a target user's friends (publicly viewable).
     *
     * TODO: Add privacy check via user-service to respect privacy settings
     *       before exposing another user's friend list.
     */
    DataResponseMessage<PageResponse<FriendshipResponse>> getUserFriends(
            Long requesterAuthUserId,
            Long targetAuthUserId,
            int page,
            int size
    );

    /**
     * Returns true if two users are currently active friends.
     */
    DataResponseMessage<Boolean> areFriends(Long firstAuthUserId, Long secondAuthUserId);

    /**
     * Returns a paginated list of mutual friends between two users.
     */
    DataResponseMessage<PageResponse<MutualFriendResponse>> getMutualFriends(
            Long requesterAuthUserId,
            Long targetAuthUserId,
            int page,
            int size
    );

    /**
     * Returns the count of mutual friends between two users.
     */
    DataResponseMessage<Long> getMutualFriendCount(
            Long requesterAuthUserId,
            Long targetAuthUserId
    );
}
