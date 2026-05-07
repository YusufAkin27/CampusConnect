package admin_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Feign client for communicating with notification-service.
 */
@FeignClient(name = "notification-service", path = "/v1/api/notifications")
public interface NotificationServiceClient {

    @PostMapping("/admin/send-to-user")
    Map<String, Object> sendToUser(@RequestBody Map<String, Object> request);

    @PostMapping("/admin/send-to-all")
    Map<String, Object> sendToAll(@RequestBody Map<String, Object> request);

    @PostMapping("/admin/send-to-department")
    Map<String, Object> sendToDepartment(@RequestBody Map<String, Object> request);

    @PostMapping("/admin/send-to-faculty")
    Map<String, Object> sendToFaculty(@RequestBody Map<String, Object> request);
}
