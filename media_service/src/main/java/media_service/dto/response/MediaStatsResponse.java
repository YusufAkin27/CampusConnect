package media_service.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaStatsResponse {

    private Long totalMediaCount;
    private Long imageCount;
    private Long videoCount;
    private Long fileCount;
    private Long totalStorageBytes;
    private Long deletedMediaCount;
    private Long temporaryMediaCount;
}
