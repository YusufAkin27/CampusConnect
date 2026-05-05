package post_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import post_service.enums.ReactionType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactPostRequest {

    @NotNull(message = "Reaction type must not be null")
    private ReactionType reactionType;
}
