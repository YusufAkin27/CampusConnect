package friend_service.controller;

import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.common.response.ResponseMessage;
import friend_service.dto.request.IgnoreSuggestionRequest;
import friend_service.dto.response.SuggestedUserResponse;
import friend_service.security.AuthUserProvider;
import friend_service.service.SuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for friend suggestion operations.
 * Base path: /v1/api/friends/suggestions
 */
@RestController
@RequestMapping("/v1/api/friends/suggestions")
@RequiredArgsConstructor
@Tag(name = "Suggestions", description = "APIs for managing friend suggestions")
public class SuggestionController {

    private final SuggestionService suggestionService;
    private final AuthUserProvider authUserProvider;

    @Operation(summary = "Get suggested users",
            description = "Returns a paginated list of suggested users for the authenticated user, " +
                    "based on mutual friends, department, faculty, and grade.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suggested users retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<DataResponseMessage<PageResponse<SuggestedUserResponse>>> getSuggestedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(suggestionService.getSuggestedUsers(authUserId, page, size));
    }

    @Operation(summary = "Hide a suggestion",
            description = "Hides a specific user from the authenticated user's suggestion list.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suggestion hidden"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/ignore")
    public ResponseEntity<ResponseMessage> ignoreSuggestion(
            @Valid @RequestBody IgnoreSuggestionRequest request,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(suggestionService.ignoreSuggestion(authUserId, request));
    }

    @Operation(summary = "Undo a hidden suggestion",
            description = "Restores a previously hidden suggestion, allowing the user to appear again.")
    @DeleteMapping("/ignore/{ignoredAuthUserId}")
    public ResponseEntity<ResponseMessage> undoIgnoreSuggestion(
            @PathVariable Long ignoredAuthUserId,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    ) {
        Long authUserId = authUserProvider.getCurrentAuthUserId(httpRequest);
        return ResponseEntity.ok(suggestionService.undoIgnoreSuggestion(authUserId, ignoredAuthUserId));
    }
}
