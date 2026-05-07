package admin_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Feign client for communicating with media-service.
 */
@FeignClient(name = "media-service", path = "/v1/api/media")
public interface MediaServiceClient {

    @GetMapping("/admin/all")
    Map<String, Object> getAllMedia(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size);

    @GetMapping("/admin/{mediaId}")
    Map<String, Object> getMediaById(@PathVariable("mediaId") Long mediaId);

    @DeleteMapping("/admin/{mediaId}")
    Map<String, Object> deleteMedia(@PathVariable("mediaId") Long mediaId);

    @GetMapping("/admin/by-user/{userId}")
    Map<String, Object> getMediaByUser(@PathVariable("userId") Long userId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size);

    @GetMapping("/admin/orphan")
    Map<String, Object> getOrphanMedia();

    @DeleteMapping("/admin/orphan/cleanup")
    Map<String, Object> cleanupOrphanMedia();

    @GetMapping("/admin/stats")
    Map<String, Object> getMediaStats();
}
