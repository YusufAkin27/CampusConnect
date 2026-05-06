package notification_service.repository;

import notification_service.entity.Notification;
import notification_service.enums.NotificationPriority;
import notification_service.enums.NotificationType;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByReceiverUserId(Long receiverUserId, Pageable pageable);

    Page<Notification> findByReceiverUserIdAndRead(Long receiverUserId, boolean read, Pageable pageable);

    Page<Notification> findByReceiverUserIdAndType(Long receiverUserId, NotificationType type, Pageable pageable);

    Page<Notification> findByReceiverUserIdAndPriority(Long receiverUserId, NotificationPriority priority, Pageable pageable);

    long countByReceiverUserIdAndRead(Long receiverUserId, boolean read);

    long deleteByReceiverUserId(Long receiverUserId);

    long deleteByReceiverUserIdAndCreatedAtBefore(Long receiverUserId, LocalDateTime before);

    long deleteByCreatedAtBefore(LocalDateTime before);

    long deleteByExpiresAtBefore(LocalDateTime before);
}
