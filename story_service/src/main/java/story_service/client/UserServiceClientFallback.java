package story_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import story_service.dto.response.UserSummaryResponse;

/**
 * Fallback implementation for UserServiceClient.
 * Returns minimal user info when user-service is unavailable.
 */
@Slf4j
@Component
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public UserSummaryResponse getUserSummary(Long userId) {
        log.warn("User service unavailable. Fallback triggered for user summary. userId={}", userId);
        return UserSummaryResponse.builder()
            .userId(userId)
            .username("unknown")
            .fullName("Unknown User")
            .profileImageUrl(null)
            .build();
    }
}
