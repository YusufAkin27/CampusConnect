package friend_service.dto.response;

import friend_service.enums.SuggestionReason;
import lombok.*;

/**
 * Response DTO for a suggested user with reasoning and relationship context.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestedUserResponse {

    private UserSummaryResponse user;
    private SuggestionReason reason;
    private Long mutualFriendCount;
    private Boolean sameFaculty;
    private Boolean sameDepartment;
    private Boolean sameGrade;
    private Boolean followedByMe;
    private Boolean requestSent;
    private Boolean requestReceived;
    private Boolean isFriend;
}
