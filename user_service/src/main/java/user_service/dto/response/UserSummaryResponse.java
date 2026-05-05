package user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.enums.Department;
import user_service.enums.Faculty;
import user_service.enums.Grade;

/**
 * Compact user summary response used in search results and lists.
 * Contains minimal information needed to display a user card.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {

    private Long id;
    private Long authUserId;
    private String username;
    private String displayName;
    private String profileImageUrl;
    private Faculty faculty;
    private Department department;
    private Grade grade;
}
