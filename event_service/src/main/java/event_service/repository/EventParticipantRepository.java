package event_service.repository;

import event_service.entity.EventParticipant;
import event_service.enums.ParticipantStatus;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {

    boolean existsByEventIdAndUserIdAndStatusIn(Long eventId, Long userId, Collection<ParticipantStatus> statuses);

    Page<EventParticipant> findByEventId(Long eventId, Pageable pageable);

    Page<EventParticipant> findByUserId(Long userId, Pageable pageable);

    Optional<EventParticipant> findByEventIdAndUserId(Long eventId, Long userId);

    long countByEventIdAndStatus(Long eventId, ParticipantStatus status);
}
