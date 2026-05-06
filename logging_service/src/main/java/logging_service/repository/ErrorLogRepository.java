package logging_service.repository;

import logging_service.entity.ErrorLog;
import logging_service.enums.ErrorSeverity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {

    Page<ErrorLog> findByResolvedFalse(Pageable pageable);

    Page<ErrorLog> findBySeverity(ErrorSeverity severity, Pageable pageable);

    Page<ErrorLog> findByServiceName(String serviceName, Pageable pageable);

    Page<ErrorLog> findByExceptionClass(String exceptionClass, Pageable pageable);

    Page<ErrorLog> findBySeverityAndResolvedFalse(ErrorSeverity severity, Pageable pageable);

    Long countByResolvedFalse();

    Long countBySeverity(ErrorSeverity severity);

    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Long countBySeverityAndCreatedAtBetween(ErrorSeverity severity, LocalDateTime start, LocalDateTime end);

    @Modifying
    @Transactional
    void deleteByCreatedAtBefore(LocalDateTime date);
}
