package event_service.dto.response;

import event_service.enums.EventCategory;
import event_service.enums.EventStatus;
import event_service.enums.EventType;
import event_service.enums.EventVisibility;
import event_service.enums.OrganizerType;
import java.time.LocalDateTime;
import java.util.List;
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
public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private String shortDescription;
    private EventCategory category;
    private EventType type;
    private EventStatus status;
    private EventVisibility visibility;
    private Long organizerId;
    private String organizerName;
    private OrganizerType organizerType;
    private String campusName;
    private String faculty;
    private String department;
    private String locationName;
    private String locationAddress;
    private String onlineUrl;
    private boolean isOnline;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime registrationStartDateTime;
    private LocalDateTime registrationEndDateTime;
    private Integer capacity;
    private Integer participantCount;
    private Integer favoriteCount;
    private Long viewCount;
    private boolean requiresApproval;
    private boolean isFeatured;
    private boolean isCancelled;
    private String cancellationReason;
    private List<EventMediaResponse> mediaList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
