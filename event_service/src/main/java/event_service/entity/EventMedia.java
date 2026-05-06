package event_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "event_media",
    indexes = {
        @Index(name = "idx_event_media_event_id", columnList = "event_id")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "media_id", nullable = false)
    private Long mediaId;

    @Column(nullable = false, length = 400)
    private String mediaUrl;

    @Column(nullable = false, length = 50)
    private String mediaType;

    @Column(nullable = false)
    private Integer orderIndex;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
