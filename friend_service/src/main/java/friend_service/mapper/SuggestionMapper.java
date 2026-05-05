package friend_service.mapper;

import friend_service.dto.response.SuggestedUserResponse;
import friend_service.dto.response.UserSummaryResponse;
import friend_service.enums.SuggestionReason;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for SuggestedUserResponse.
 */
@Component
public class SuggestionMapper {

    /**
     * Builds a SuggestedUserResponse for a candidate user with suggestion context.
     *
     * @param user           the candidate user's summary
     * @param reason         the primary reason for the suggestion
     * @param mutualFriendCount number of mutual friends
     * @param sameFaculty    whether they share the same faculty
     * @param sameDepartment whether they share the same department
     * @param sameGrade      whether they share the same grade/year
     * @param followedByMe   whether the current user already follows this user
     * @param requestSent    whether a pending request was already sent
     * @param requestReceived whether the candidate sent a pending request
     * @param isFriend       whether they are already friends (should be false for suggestions)
     */
    public SuggestedUserResponse toSuggestedUserResponse(
            UserSummaryResponse user,
            SuggestionReason reason,
            Long mutualFriendCount,
            Boolean sameFaculty,
            Boolean sameDepartment,
            Boolean sameGrade,
            Boolean followedByMe,
            Boolean requestSent,
            Boolean requestReceived,
            Boolean isFriend
    ) {
        return SuggestedUserResponse.builder()
                .user(user)
                .reason(reason)
                .mutualFriendCount(mutualFriendCount)
                .sameFaculty(sameFaculty)
                .sameDepartment(sameDepartment)
                .sameGrade(sameGrade)
                .followedByMe(followedByMe)
                .requestSent(requestSent)
                .requestReceived(requestReceived)
                .isFriend(isFriend)
                .build();
    }
}
