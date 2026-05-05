package friend_service.mapper;

import friend_service.dto.response.FollowResponse;
import friend_service.dto.response.FollowerResponse;
import friend_service.dto.response.FollowingResponse;
import friend_service.dto.response.UserSummaryResponse;
import friend_service.entity.Follow;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for Follow entity to response DTOs.
 */
@Component
public class FollowMapper {

    /**
     * Maps a Follow entity to FollowResponse with full follower and following profiles.
     */
    public FollowResponse toFollowResponse(
            Follow follow,
            UserSummaryResponse follower,
            UserSummaryResponse following
    ) {
        return FollowResponse.builder()
                .id(follow.getId())
                .follower(follower)
                .following(following)
                .status(follow.getStatus())
                .createdAt(follow.getCreatedAt())
                .build();
    }

    /**
     * Maps a Follow entity (follower perspective) to FollowerResponse with relation context.
     *
     * @param follow          the Follow entity
     * @param user            the follower's user summary
     * @param isFriend        is the follower also a friend of the current user
     * @param followedByMe    does the current user follow this follower back
     * @param followsMe       true (they follow the current user)
     * @param mutualFriendCount number of mutual friends
     */
    public FollowerResponse toFollowerResponse(
            Follow follow,
            UserSummaryResponse user,
            Boolean isFriend,
            Boolean followedByMe,
            Boolean followsMe,
            Long mutualFriendCount
    ) {
        return FollowerResponse.builder()
                .user(user)
                .isFriend(isFriend)
                .followedByMe(followedByMe)
                .followsMe(followsMe)
                .mutualFriendCount(mutualFriendCount)
                .followedAt(follow.getCreatedAt())
                .build();
    }

    /**
     * Maps a Follow entity (following perspective) to FollowingResponse with relation context.
     *
     * @param follow          the Follow entity
     * @param user            the followed user's summary
     * @param isFriend        is the followed user also a friend of the current user
     * @param followedByMe    true (the current user follows them)
     * @param followsMe       does the followed user follow the current user back
     * @param mutualFriendCount number of mutual friends
     */
    public FollowingResponse toFollowingResponse(
            Follow follow,
            UserSummaryResponse user,
            Boolean isFriend,
            Boolean followedByMe,
            Boolean followsMe,
            Long mutualFriendCount
    ) {
        return FollowingResponse.builder()
                .user(user)
                .isFriend(isFriend)
                .followedByMe(followedByMe)
                .followsMe(followsMe)
                .mutualFriendCount(mutualFriendCount)
                .followedAt(follow.getCreatedAt())
                .build();
    }
}
