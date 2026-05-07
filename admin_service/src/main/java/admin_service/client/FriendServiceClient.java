package admin_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Feign client for communicating with friend-service.
 */
@FeignClient(name = "friend-service", path = "/v1/api/friends")
public interface FriendServiceClient {

    @GetMapping("/admin/{userId}/friends")
    Map<String, Object> getUserFriends(@PathVariable("userId") Long userId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size);
}
