package friend_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for hiding a user from the suggestions list.
 */
@Data
public class IgnoreSuggestionRequest {

    @NotNull(message = "ignoredAuthUserId must not be null")
    private Long ignoredAuthUserId;
}
