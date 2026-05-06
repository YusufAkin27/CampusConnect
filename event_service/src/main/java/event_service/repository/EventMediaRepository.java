package event_service.repository;

import event_service.entity.EventMedia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventMediaRepository extends JpaRepository<EventMedia, Long> {

    List<EventMedia> findByEventId(Long eventId);

    List<EventMedia> findByEventIdOrderByOrderIndexAsc(Long eventId);

    void deleteByEventId(Long eventId);
}
