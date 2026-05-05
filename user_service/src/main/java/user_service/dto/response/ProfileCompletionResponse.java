package user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Profile completion check response.
 * Shows how complete the user's profile is and which fields are missing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileCompletionResponse {

    private Long userId;

    private Boolean completed;

    /**
     * Completion percentage from 0 to 100.
     */
    private Integer completionRate;

    /**
     * List of field names that are missing or empty.
     */
    private List<String> missingFields;
}
