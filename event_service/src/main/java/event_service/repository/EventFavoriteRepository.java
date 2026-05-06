package event_service.repository;

import event_service.entity.EventFavorite;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventFavoriteRepository extends JpaRepository<EventFavorite, Long> {

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    Page<EventFavorite> findByUserId(Long userId, Pageable pageable);

    Optional<EventFavorite> findByEventIdAndUserId(Long eventId, Long userId);

    void deleteByEventIdAndUserId(Long eventId, Long userId);

    long countByEventId(Long eventId);
}
