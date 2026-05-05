package friend_service.mapper;

import friend_service.dto.response.FriendshipResponse;
import friend_service.dto.response.MutualFriendResponse;
import friend_service.dto.response.UserSummaryResponse;
import friend_service.entity.Friendship;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for Friendship entity to response DTOs.
 */
@Component
public class FriendshipMapper {

    /**
     * Maps a Friendship entity to FriendshipResponse from the perspective of a specific user.
     *
     * @param friendship       the Friendship entity
     * @param currentAuthUserId the viewing user's authUserId
     * @param friend           the other user's UserSummaryResponse
     * @return the populated FriendshipResponse
     */
    public FriendshipResponse toFriendshipResponse(
            Friendship friendship,
            Long currentAuthUserId,
            UserSummaryResponse friend
    ) {
        return FriendshipResponse.builder()
                .id(friendship.getId())
                .friend(friend)
                .status(friendship.getStatus())
                .createdAt(friendship.getCreatedAt())
                .build();
    }

    /**
     * Maps a mutual friend (represented as a UserSummaryResponse) with the friendship date.
     *
     * @param user                  the mutual friend's user summary
     * @param friendshipCreatedAt   when the friendship with this user was established
     * @return the MutualFriendResponse
     */
    public MutualFriendResponse toMutualFriendResponse(
            UserSummaryResponse user,
            java.time.LocalDateTime friendshipCreatedAt
    ) {
        return MutualFriendResponse.builder()
                .user(user)
                .friendshipCreatedAt(friendshipCreatedAt)
                .build();
    }
}
