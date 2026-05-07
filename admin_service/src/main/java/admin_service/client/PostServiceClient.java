package admin_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Feign client for communicating with post-service.
 */
@FeignClient(name = "post-service", path = "/v1/api/posts")
public interface PostServiceClient {

    @GetMapping("/admin/all")
    Map<String, Object> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size);

    @GetMapping("/admin/{postId}")
    Map<String, Object> getPostById(@PathVariable("postId") Long postId);

    @DeleteMapping("/admin/{postId}")
    Map<String, Object> deletePost(@PathVariable("postId") Long postId);

    @PatchMapping("/admin/{postId}/hide")
    Map<String, Object> hidePost(@PathVariable("postId") Long postId);

    @PatchMapping("/admin/{postId}/unhide")
    Map<String, Object> unhidePost(@PathVariable("postId") Long postId);

    @GetMapping("/admin/reported")
    Map<String, Object> getReportedPosts(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size);

    @GetMapping("/admin/by-user/{userId}")
    Map<String, Object> getPostsByUser(@PathVariable("userId") Long userId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size);

    @GetMapping("/admin/stats")
    Map<String, Object> getPostStats();
}
