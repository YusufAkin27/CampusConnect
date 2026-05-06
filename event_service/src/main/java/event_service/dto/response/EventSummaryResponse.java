package event_service.dto.response;

import event_service.enums.EventCategory;
import event_service.enums.EventStatus;
import event_service.enums.EventType;
import java.time.LocalDateTime;
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
public class EventSummaryResponse {

    private Long id;
    private String title;
    private String shortDescription;
    private EventCategory category;
    private EventType type;
    private EventStatus status;
    private String locationName;
    private boolean isOnline;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer capacity;
    private Integer participantCount;
    private Integer favoriteCount;
    private String coverMediaUrl;
}
