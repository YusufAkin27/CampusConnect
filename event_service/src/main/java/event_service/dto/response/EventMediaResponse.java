package event_service.dto.response;

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
public class EventMediaResponse {

    private Long mediaId;
    private String mediaUrl;
    private String mediaType;
    private Integer orderIndex;
}
