package friend_service.controller;

import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.dto.response.RelationStatusResponse;
import friend_service.dto.response.SocialStatsResponse;
import friend_service.dto.response.UserRelationResponse;
import friend_service.security.AuthUserProvider;
import friend_service.service.SocialGraphService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for social graph and search operations.
 * Base path: /v1/api/friends/social
 */
@RestController
@RequestMapping("/v1/api/friends/social")
@RequiredArgsConstructor
@Tag(name = "Social Graph", description = "User search, relation status, and social statistics")
public class SocialGraphController {

    private final SocialGraphService socialGraphService;
    private final AuthUserProvider authUserProvider;

    @Operation(summary = "Search users with relation status",
            description = "Searches users via user-service and enriches results with social relationship context.")
    @GetMapping("/search")
    public ResponseEntity<DataResponseMessage<PageResponse<UserRelationResponse>>> searchUsersWithRelation(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String faculty,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String grade,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(socialGraphService.searchUsersWithRelation(
                authUserId, keyword, faculty, department, grade, page, size));
    }

    @Operation(summary = "Get relation status with a user",
            description = "Returns the complete social relation between the authenticated user and target.")
    @GetMapping("/relation/{targetAuthUserId}")
    public ResponseEntity<DataResponseMessage<RelationStatusResponse>> getRelationStatus(
            @PathVariable Long targetAuthUserId,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(socialGraphService.getRelationStatus(authUserId, targetAuthUserId));
    }

    @Operation(summary = "Get social stats for a specific user")
    @GetMapping("/stats/{targetAuthUserId}")
    public ResponseEntity<DataResponseMessage<SocialStatsResponse>> getSocialStatsForUser(
            @PathVariable Long targetAuthUserId,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(socialGraphService.getSocialStats(targetAuthUserId));
    }

    @Operation(summary = "Get my social stats",
            description = "Returns the authenticated user's social statistics.")
    @GetMapping("/stats/me")
    public ResponseEntity<DataResponseMessage<SocialStatsResponse>> getMySocialStats(
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(socialGraphService.getSocialStats(authUserId));
    }
}
