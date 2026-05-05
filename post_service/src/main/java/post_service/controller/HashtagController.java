package post_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import post_service.common.response.DataResponseMessage;
import post_service.common.response.PageResponse;
import post_service.dto.response.HashtagResponse;
import post_service.dto.response.PostResponse;
import post_service.security.AuthUserProvider;
import post_service.service.HashtagService;

import java.util.List;

@RestController
@RequestMapping("/v1/api/posts/hashtags")
@RequiredArgsConstructor
@Tag(name = "Hashtag", description = "Hashtag search and trending endpoints")
public class HashtagController {

    private final HashtagService hashtagService;
    private final AuthUserProvider authUserProvider;

    @GetMapping("/search")
    @Operation(summary = "Search hashtags", description = "Searches hashtags by keyword.")
    public ResponseEntity<DataResponseMessage<PageResponse<HashtagResponse>>> searchHashtags(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(hashtagService.searchHashtags(keyword, page, size));
    }

    @GetMapping("/trending")
    @Operation(summary = "Get trending hashtags", description = "Returns the top 10 trending hashtags.")
    public ResponseEntity<DataResponseMessage<List<HashtagResponse>>> getTrendingHashtags() {
        return ResponseEntity.ok(hashtagService.getTrendingHashtags());
    }

    @GetMapping("/{hashtag}/posts")
    @Operation(summary = "Get posts by hashtag", description = "Returns posts tagged with the specified hashtag.")
    public ResponseEntity<DataResponseMessage<PageResponse<PostResponse>>> getPostsByHashtag(
            @PathVariable String hashtag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        Long authUserId = tryGetAuthUserId(httpRequest);
        return ResponseEntity.ok(hashtagService.getPostsByHashtag(authUserId, hashtag, page, size));
    }

    private Long tryGetAuthUserId(HttpServletRequest httpRequest) {
        try {
            return authUserProvider.getCurrentAuthUserId(httpRequest);
        } catch (Exception e) {
            return null;
        }
    }
}
