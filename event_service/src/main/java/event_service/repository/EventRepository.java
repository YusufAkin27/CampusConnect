package event_service.repository;

import event_service.entity.Event;
import event_service.enums.EventCategory;
import event_service.enums.EventStatus;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    Page<Event> findByCategory(EventCategory category, Pageable pageable);

    Page<Event> findByOrganizerId(Long organizerId, Pageable pageable);

    Page<Event> findByStartDateTimeAfter(LocalDateTime startDateTime, Pageable pageable);

    Page<Event> findByCampusNameIgnoreCase(String campusName, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Event> findByIdForUpdate(Long id);

    @Query("select e from Event e order by (e.participantCount + e.favoriteCount + e.viewCount) desc")
    Page<Event> findPopularEvents(Pageable pageable);
}
