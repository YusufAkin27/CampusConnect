package event_service.dto.request;

import event_service.enums.EventCategory;
import event_service.enums.EventStatus;
import event_service.enums.EventType;
import java.time.LocalDate;
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
public class SearchEventRequest {

    private String keyword;
    private EventCategory category;
    private EventType type;
    private EventStatus status;
    private String campusName;
    private String faculty;
    private String department;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean onlyOnline;
    private Boolean onlyFeatured;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
