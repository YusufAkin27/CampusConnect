package notification_service.client.dto;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LogEventRequest {
    private String eventType;
    private Long actorUserId;
    private String referenceId;
    private String message;
    private Map<String, Object> metadata;
    private LocalDateTime timestamp;
}
