package notification_service.repository;

import notification_service.entity.NotificationDeliveryLog;
import notification_service.enums.DeliveryStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationDeliveryLogRepository extends JpaRepository<NotificationDeliveryLog, Long> {

    List<NotificationDeliveryLog> findByStatus(DeliveryStatus status);

    List<NotificationDeliveryLog> findByStatusIn(List<DeliveryStatus> statuses);

    Optional<NotificationDeliveryLog> findTopByNotificationIdOrderByCreatedAtDesc(Long notificationId);
}
