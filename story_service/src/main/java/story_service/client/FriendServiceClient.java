package story_service.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client for communicating with the friend-service.
 * Handles follower/following relationship checks for story visibility enforcement.
 */
@FeignClient(
    name = "${friend.service.name}",
    fallback = FriendServiceClientFallback.class
)
public interface FriendServiceClient {

    /**
     * Retrieves the list of user IDs that the given user follows.
     *
     * @param userId the user ID whose following list is requested
     * @return list of followed user IDs
     */
    @GetMapping("/v1/api/friends/internal/{userId}/following-ids")
    List<Long> getFollowingUserIds(@PathVariable("userId") Long userId);

    /**
     * Checks if the source user is following the target user.
     *
     * @param sourceUserId the user who might be following
     * @param targetUserId the user who might be followed
     * @return true if sourceUserId follows targetUserId
     */
    @GetMapping("/v1/api/friends/internal/is-following")
    boolean isFollowing(
        @RequestParam("sourceUserId") Long sourceUserId,
        @RequestParam("targetUserId") Long targetUserId
    );
}
