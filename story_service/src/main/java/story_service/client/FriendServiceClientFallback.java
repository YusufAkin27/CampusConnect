package story_service.client;

import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for FriendServiceClient.
 * When friend-service is unavailable:
 * - getFollowingUserIds returns empty list (no feed results, but no error)
 * - isFollowing returns false (conservative: deny access to FOLLOWERS_ONLY stories)
 */
@Slf4j
@Component
public class FriendServiceClientFallback implements FriendServiceClient {

    @Override
    public List<Long> getFollowingUserIds(Long userId) {
        log.warn("Friend service unavailable. Fallback triggered for following list. userId={}", userId);
        return Collections.emptyList();
    }

    @Override
    public boolean isFollowing(Long sourceUserId, Long targetUserId) {
        log.warn("Friend service unavailable. Fallback triggered for follow check. source={}, target={}", sourceUserId, targetUserId);
        return false;
    }
}
