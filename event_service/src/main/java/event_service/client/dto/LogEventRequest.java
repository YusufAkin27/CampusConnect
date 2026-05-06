package event_service.client.dto;

import java.time.LocalDateTime;
import java.util.Map;
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
public class LogEventRequest {

    private String eventType;
    private Long actorUserId;
    private Long referenceId;
    private String message;
    private Map<String, Object> metadata;
    private LocalDateTime timestamp;
}
