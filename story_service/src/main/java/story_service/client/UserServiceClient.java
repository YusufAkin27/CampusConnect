package story_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import story_service.dto.response.UserSummaryResponse;

/**
 * Feign client for communicating with the user-service.
 * Retrieves user summary information by user ID.
 */
@FeignClient(
    name = "${user.service.name}",
    fallback = UserServiceClientFallback.class
)
public interface UserServiceClient {

    /**
     * Retrieves user summary (username, full name, profile image) by user ID.
     *
     * @param userId the user ID
     * @return UserSummaryResponse with user details
     */
    @GetMapping("/v1/api/users/internal/{userId}/summary")
    UserSummaryResponse getUserSummary(@PathVariable("userId") Long userId);
}
