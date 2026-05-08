package story_service.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import story_service.enums.MediaType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorySummaryResponse {

    private UUID id;
    private Long ownerUserId;
    private String ownerUsername;
    private String mediaUrl;
    private MediaType mediaType;
    private String caption;
    private boolean viewed;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
