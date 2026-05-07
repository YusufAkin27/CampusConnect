package admin_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Feign client for communicating with user-service.
 * Uses Consul service discovery for URL resolution.
 */
@FeignClient(name = "user-service", path = "/v1/api/users")
public interface UserServiceClient {

    @GetMapping("/internal/all")
    Map<String, Object> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size);

    @GetMapping("/internal/{userId}")
    Map<String, Object> getUserById(@PathVariable("userId") Long userId);

    @GetMapping("/internal/search")
    Map<String, Object> searchUsers(@RequestParam("keyword") String keyword,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size);

    @PatchMapping("/admin/{userId}/status")
    Map<String, Object> updateUserStatus(@PathVariable("userId") Long userId,
                                          @RequestBody Map<String, String> statusUpdate);

    @DeleteMapping("/admin/{userId}")
    Map<String, Object> deleteUser(@PathVariable("userId") Long userId);

    @GetMapping("/internal/{userId}/activity")
    Map<String, Object> getUserActivity(@PathVariable("userId") Long userId);

    @GetMapping("/admin/stats")
    Map<String, Object> getUserStats();
}
