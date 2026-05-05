package friend_service.mapper;

import friend_service.dto.response.*;
import friend_service.enums.FriendRelationStatus;
import friend_service.enums.SuggestionReason;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for social graph response DTOs.
 */
@Component
public class SocialGraphMapper {

    /**
     * Builds a UserRelationResponse combining a user summary with relation context flags.
     */
    public UserRelationResponse toUserRelationResponse(
            UserSummaryResponse user,
            FriendRelationStatus relationStatus,
            Boolean isFriend,
            Boolean requestSent,
            Boolean requestReceived,
            Boolean following,
            Boolean followedByMe,
            Boolean followsMe,
            Long mutualFriendCount
    ) {
        return UserRelationResponse.builder()
                .user(user)
                .relationStatus(relationStatus)
                .isFriend(isFriend)
                .requestSent(requestSent)
                .requestReceived(requestReceived)
                .following(following)
                .followedByMe(followedByMe)
                .followsMe(followsMe)
                .mutualFriendCount(mutualFriendCount)
                .build();
    }

    /**
     * Builds a RelationStatusResponse for the relation-status endpoint.
     */
    public RelationStatusResponse toRelationStatusResponse(
            Long requesterAuthUserId,
            Long targetAuthUserId,
            FriendRelationStatus relationStatus,
            Boolean isFriend,
            Boolean requestSent,
            Boolean requestReceived,
            Boolean following,
            Boolean followsMe,
            Long mutualFriendCount
    ) {
        return RelationStatusResponse.builder()
                .requesterAuthUserId(requesterAuthUserId)
                .targetAuthUserId(targetAuthUserId)
                .relationStatus(relationStatus)
                .isFriend(isFriend)
                .requestSent(requestSent)
                .requestReceived(requestReceived)
                .following(following)
                .followsMe(followsMe)
                .mutualFriendCount(mutualFriendCount)
                .build();
    }

    /**
     * Builds a SocialStatsResponse for a given user.
     */
    public SocialStatsResponse toSocialStatsResponse(
            Long authUserId,
            Long friendCount,
            Long followerCount,
            Long followingCount,
            Long pendingReceivedRequestCount,
            Long pendingSentRequestCount
    ) {
        return SocialStatsResponse.builder()
                .authUserId(authUserId)
                .friendCount(friendCount)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .pendingReceivedRequestCount(pendingReceivedRequestCount)
                .pendingSentRequestCount(pendingSentRequestCount)
                .build();
    }
}
