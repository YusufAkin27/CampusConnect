package story_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaValidationResponse {

    private Long mediaId;
    private boolean valid;
    private Long ownerUserId;
    private String mediaUrl;
    private String mediaType;
    private String message;
}
