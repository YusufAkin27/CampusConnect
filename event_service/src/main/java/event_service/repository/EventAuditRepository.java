package event_service.repository;

import event_service.entity.EventAudit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventAuditRepository extends JpaRepository<EventAudit, Long> {

    List<EventAudit> findByEventIdOrderByCreatedAtDesc(Long eventId);
}
