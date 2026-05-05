package post_service.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentionResponse {

    private Long mentionedAuthUserId;
    private String username;
}
