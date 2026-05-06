package event_service.dto.request;

import event_service.enums.EventCategory;
import event_service.enums.EventType;
import event_service.enums.EventVisibility;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class UpdateEventRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    private String description;

    @Size(max = 500)
    private String shortDescription;

    @NotNull
    private EventCategory category;

    @NotNull
    private EventType type;

    @NotNull
    private EventVisibility visibility;

    @Size(max = 120)
    private String campusName;

    @Size(max = 120)
    private String faculty;

    @Size(max = 120)
    private String department;

    @Size(max = 150)
    private String locationName;

    @Size(max = 250)
    private String locationAddress;

    @Size(max = 400)
    private String onlineUrl;

    @NotNull
    @Future
    private LocalDateTime startDateTime;

    @NotNull
    @Future
    private LocalDateTime endDateTime;

    @FutureOrPresent
    private LocalDateTime registrationStartDateTime;

    private LocalDateTime registrationEndDateTime;

    private Integer capacity;

    @NotNull
    private Boolean requiresApproval;

    private List<Long> mediaIds;
}
