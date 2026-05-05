package post_service.dto.response;

import lombok.*;
import post_service.enums.MediaType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostMediaResponse {

    private Long id;
    private String mediaUrl;
    private MediaType mediaType;
    private String thumbnailUrl;
    private Integer displayOrder;
    private Long fileSize;
    private String mimeType;
    private Integer width;
    private Integer height;
    private Double duration;
}
