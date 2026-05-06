package logging_service.repository;

import logging_service.entity.SecurityLog;
import logging_service.enums.SecurityEventType;
import logging_service.enums.SecuritySeverity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface SecurityLogRepository extends JpaRepository<SecurityLog, Long> {

    Page<SecurityLog> findByAuthUserId(Long authUserId, Pageable pageable);

    Page<SecurityLog> findByEventType(SecurityEventType eventType, Pageable pageable);

    Page<SecurityLog> findBySeverity(SecuritySeverity severity, Pageable pageable);

    Page<SecurityLog> findByClientIp(String clientIp, Pageable pageable);

    Long countBySeverity(SecuritySeverity severity);

    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Long countBySeverityAndCreatedAtBetween(SecuritySeverity severity, LocalDateTime start, LocalDateTime end);

    @Modifying
    @Transactional
    void deleteByCreatedAtBefore(LocalDateTime date);
}
