package notification_service.client;

import notification_service.client.dto.UserSummary;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${user.service.name}")
public interface UserClient {

    @GetMapping("/v1/api/users/{userId}")
    UserSummary getUserById(@PathVariable("userId") Long userId);

    @PostMapping("/v1/api/users/internal/bulk")
    List<UserSummary> getUsersByIds(@RequestBody List<Long> userIds);

    @GetMapping("/v1/api/users/{userId}/exists")
    ExistsResponse existsUser(@PathVariable("userId") Long userId);

    class ExistsResponse {
        public boolean exists;
    }
}
