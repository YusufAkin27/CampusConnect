package post_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import post_service.common.response.DataResponseMessage;
import post_service.common.response.PageResponse;
import post_service.common.response.ResponseMessage;
import post_service.dto.response.PostResponse;
import post_service.security.AuthUserProvider;
import post_service.service.SavedPostService;

@RestController
@RequestMapping("/v1/api/posts/saved")
@RequiredArgsConstructor
@Tag(name = "Saved Post", description = "Saved post management endpoints")
public class SavedPostController {

    private final SavedPostService savedPostService;
    private final AuthUserProvider authUserProvider;

    @PostMapping("/{postId}")
    @Operation(summary = "Save post", description = "Saves a post to the authenticated user's collection.")
    public ResponseEntity<ResponseMessage> savePost(
            @PathVariable Long postId,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(savedPostService.savePost(authUserId, postId));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Unsave post", description = "Removes a post from the authenticated user's saved collection.")
    public ResponseEntity<ResponseMessage> unsavePost(
            @PathVariable Long postId,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(savedPostService.unsavePost(authUserId, postId));
    }

    @GetMapping("/me")
    @Operation(summary = "Get saved posts", description = "Returns the authenticated user's saved posts.")
    public ResponseEntity<DataResponseMessage<PageResponse<PostResponse>>> getSavedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(savedPostService.getSavedPosts(authUserId, page, size));
    }
}
