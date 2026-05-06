package event_service.dto.response;

import event_service.enums.ParticipantStatus;
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
public class EventParticipantResponse {

    private Long id;
    private Long eventId;
    private Long userId;
    private String username;
    private String fullName;
    private ParticipantStatus status;
    private LocalDateTime joinedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
}
