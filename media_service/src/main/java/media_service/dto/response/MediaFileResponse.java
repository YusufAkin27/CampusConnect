package media_service.dto.response;

import lombok.*;
import media_service.enums.MediaContext;
import media_service.enums.MediaStatus;
import media_service.enums.MediaType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFileResponse {

    private Long id;
    private Long ownerAuthUserId;
    private String originalFilename;
    private String storedFilename;
    private String storageKey;
    private String mediaUrl;
    private String thumbnailUrl;
    private MediaType mediaType;
    private MediaContext mediaContext;
    private MediaStatus status;
    private String mimeType;
    private String extension;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private Double duration;
    private Boolean publicAccessible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
