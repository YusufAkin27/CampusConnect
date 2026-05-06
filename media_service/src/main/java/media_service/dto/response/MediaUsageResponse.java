package media_service.dto.response;

import lombok.*;
import media_service.enums.MediaUsageStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaUsageResponse {

    private Long id;
    private Long mediaFileId;
    private String serviceName;
    private String targetType;
    private Long targetId;
    private Long authUserId;
    private MediaUsageStatus status;
    private LocalDateTime createdAt;
}
