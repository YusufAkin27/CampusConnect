package admin_service.controller;

import admin_service.common.response.DataResponseMessage;
import admin_service.service.PostAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/admin/posts")
@RequiredArgsConstructor
@Tag(name = "Post Administration", description = "Manage platform posts from admin panel")
public class PostAdminController {

    private final PostAdminService postAdminService;

    @GetMapping
    @PreAuthorize("hasAuthority('POST_VIEW')")
    @Operation(summary = "List all posts")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getAllPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(DataResponseMessage.success("Posts retrieved.", postAdminService.getAllPosts(page, size)));
    }

    @GetMapping("/{postId}")
    @PreAuthorize("hasAuthority('POST_VIEW')")
    @Operation(summary = "Get post details")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(DataResponseMessage.success("Post retrieved.", postAdminService.getPostById(postId)));
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAuthority('POST_DELETE')")
    @Operation(summary = "Delete a post")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> deletePost(@PathVariable Long postId) {
        return ResponseEntity.ok(DataResponseMessage.success("Post deleted.", postAdminService.deletePost(postId)));
    }

    @PatchMapping("/{postId}/hide")
    @PreAuthorize("hasAuthority('POST_HIDE')")
    @Operation(summary = "Hide a post")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> hidePost(@PathVariable Long postId) {
        return ResponseEntity.ok(DataResponseMessage.success("Post hidden.", postAdminService.hidePost(postId)));
    }

    @PatchMapping("/{postId}/unhide")
    @PreAuthorize("hasAuthority('POST_HIDE')")
    @Operation(summary = "Unhide a post")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> unhidePost(@PathVariable Long postId) {
        return ResponseEntity.ok(DataResponseMessage.success("Post unhidden.", postAdminService.unhidePost(postId)));
    }

    @GetMapping("/reported")
    @PreAuthorize("hasAuthority('POST_VIEW')")
    @Operation(summary = "Get reported posts")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getReportedPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(DataResponseMessage.success("Reported posts.", postAdminService.getReportedPosts(page, size)));
    }

    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAuthority('POST_VIEW')")
    @Operation(summary = "Get posts by user")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getPostsByUser(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(DataResponseMessage.success("User posts.", postAdminService.getPostsByUser(userId, page, size)));
    }
}
