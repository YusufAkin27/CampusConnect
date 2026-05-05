package friend_service.service;

import friend_service.common.response.DataResponseMessage;
import friend_service.common.response.PageResponse;
import friend_service.common.response.ResponseMessage;
import friend_service.dto.request.IgnoreSuggestionRequest;
import friend_service.dto.response.SuggestedUserResponse;

/**
 * Service interface for managing friend suggestions.
 */
public interface SuggestionService {

    /**
     * Returns a paginated list of suggested users based on:
     * - Mutual friends
     * - Same department / faculty / grade
     * - Popular users in the university
     *
     * Excludes: existing friends, pending request targets/receivers,
     * already-following users, self, and ignored suggestions.
     */
    DataResponseMessage<PageResponse<SuggestedUserResponse>> getSuggestedUsers(
            Long authUserId,
            int page,
            int size
    );

    /**
     * Hides a specific user from the suggestion list.
     */
    ResponseMessage ignoreSuggestion(Long authUserId, IgnoreSuggestionRequest request);

    /**
     * Restores a previously hidden suggestion (removes the ignore record).
     */
    ResponseMessage undoIgnoreSuggestion(Long authUserId, Long ignoredAuthUserId);
}
