package friend_service.service;

import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.common.response.ResponseMessage;
import friend_service.dto.response.FollowResponse;
import friend_service.dto.response.FollowerResponse;
import friend_service.dto.response.FollowingResponse;

/**
 * Service interface for managing follow/unfollow operations and follower lists.
 */
public interface FollowService {

    /**
     * Follows a target user. Reactivates an existing UNFOLLOWED record if present.
     */
    DataResponseMessage<FollowResponse> followUser(
            Long followerAuthUserId,
            Long followingAuthUserId
    );

    /**
     * Unfollows a target user (soft-delete: status = UNFOLLOWED).
     */
    ResponseMessage unfollowUser(
            Long followerAuthUserId,
            Long followingAuthUserId
    );

    /**
     * Returns a paginated list of users who follow the given user.
     */
    DataResponseMessage<PageResponse<FollowerResponse>> getFollowers(
            Long authUserId,
            int page,
            int size
    );

    /**
     * Returns a paginated list of users that the given user follows.
     */
    DataResponseMessage<PageResponse<FollowingResponse>> getFollowing(
            Long authUserId,
            int page,
            int size
    );

    /**
     * Checks whether the follower is actively following the target.
     */
    DataResponseMessage<Boolean> isFollowing(
            Long followerAuthUserId,
            Long followingAuthUserId
    );
}
