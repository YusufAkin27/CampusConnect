package event_service.entity;

import event_service.enums.ParticipantStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "event_participants",
    indexes = {
        @Index(name = "idx_event_participants_event_id", columnList = "event_id"),
        @Index(name = "idx_event_participants_user_id", columnList = "user_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_event_participants_event_user", columnNames = {"event_id", "user_id"})
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 120)
    private String username;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ParticipantStatus status;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime rejectedAt;

    @Column(length = 250)
    private String cancelReason;
}
