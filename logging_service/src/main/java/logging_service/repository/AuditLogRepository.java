package logging_service.repository;

import logging_service.entity.AuditLog;
import logging_service.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByAuthUserId(Long authUserId, Pageable pageable);

    Page<AuditLog> findByAction(AuditAction action, Pageable pageable);

    Page<AuditLog> findByTargetTypeAndTargetId(String targetType, String targetId, Pageable pageable);

    Page<AuditLog> findByServiceName(String serviceName, Pageable pageable);

    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Modifying
    @Transactional
    void deleteByCreatedAtBefore(LocalDateTime date);
}
