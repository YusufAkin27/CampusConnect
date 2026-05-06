package media_service.dto.response;

import lombok.*;
import media_service.enums.MediaContext;
import media_service.enums.MediaType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaSummaryResponse {

    private Long id;
    private String mediaUrl;
    private String thumbnailUrl;
    private MediaType mediaType;
    private MediaContext mediaContext;
    private String mimeType;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private Double duration;
}
