package post_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import post_service.enums.MediaType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostMediaRequest {

    @NotBlank(message = "Media URL must not be blank")
    private String mediaUrl;

    @NotNull(message = "Media type must not be null")
    private MediaType mediaType;

    private String thumbnailUrl;
    private Integer displayOrder;
    private Long fileSize;
    private String mimeType;
    private Integer width;
    private Integer height;
    private Double duration;
}
