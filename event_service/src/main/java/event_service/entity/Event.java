package event_service.entity;

import event_service.enums.EventCategory;
import event_service.enums.EventStatus;
import event_service.enums.EventType;
import event_service.enums.EventVisibility;
import event_service.enums.OrganizerType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "events",
    indexes = {
        @Index(name = "idx_events_status", columnList = "status"),
        @Index(name = "idx_events_category", columnList = "category"),
        @Index(name = "idx_events_start_date", columnList = "start_date_time"),
        @Index(name = "idx_events_organizer_id", columnList = "organizer_id"),
        @Index(name = "idx_events_campus", columnList = "campus_name"),
        @Index(name = "idx_events_faculty", columnList = "faculty"),
        @Index(name = "idx_events_department", columnList = "department")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String shortDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventVisibility visibility;

    @Column(nullable = false)
    private Long organizerId;

    @Column(nullable = false, length = 150)
    private String organizerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrganizerType organizerType;

    @Column(length = 120)
    private String campusName;

    @Column(length = 120)
    private String faculty;

    @Column(length = 120)
    private String department;

    @Column(length = 150)
    private String locationName;

    @Column(length = 250)
    private String locationAddress;

    @Column(length = 400)
    private String onlineUrl;

    @Column(nullable = false)
    private boolean isOnline;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "registration_start_date_time")
    private LocalDateTime registrationStartDateTime;

    @Column(name = "registration_end_date_time")
    private LocalDateTime registrationEndDateTime;

    private Integer capacity;

    @Column(nullable = false)
    private Integer participantCount;

    @Column(nullable = false)
    private Integer favoriteCount;

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private boolean requiresApproval;

    @Column(nullable = false)
    private boolean isFeatured;

    @Column(nullable = false)
    private boolean isCancelled;

    @Column(length = 250)
    private String cancellationReason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (participantCount == null) {
            participantCount = 0;
        }
        if (favoriteCount == null) {
            favoriteCount = 0;
        }
        if (viewCount == null) {
            viewCount = 0L;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
