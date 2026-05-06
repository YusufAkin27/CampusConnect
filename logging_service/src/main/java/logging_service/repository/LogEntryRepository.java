package logging_service.repository;

import logging_service.entity.LogEntry;
import logging_service.enums.LogCategory;
import logging_service.enums.LogLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {

    Page<LogEntry> findByServiceName(String serviceName, Pageable pageable);

    Page<LogEntry> findByLevel(LogLevel level, Pageable pageable);

    Page<LogEntry> findByCategory(LogCategory category, Pageable pageable);

    Page<LogEntry> findByAuthUserId(Long authUserId, Pageable pageable);

    Page<LogEntry> findByTraceId(String traceId, Pageable pageable);

    Page<LogEntry> findByCorrelationId(String correlationId, Pageable pageable);

    Long countByLevel(LogLevel level);

    Long countByCategory(LogCategory category);

    Long countByServiceName(String serviceName);

    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Modifying
    @Transactional
    void deleteByCreatedAtBefore(LocalDateTime date);

    /**
     * Advanced search with all optional filters.
     * All parameters are optional - pass null to skip filtering on that field.
     * Keyword searches in message and details fields.
     */
    @Query("""
            SELECT l FROM LogEntry l
            WHERE (:serviceName IS NULL OR l.serviceName = :serviceName)
            AND (:level IS NULL OR l.level = :level)
            AND (:category IS NULL OR l.category = :category)
            AND (:authUserId IS NULL OR l.authUserId = :authUserId)
            AND (:traceId IS NULL OR l.traceId = :traceId)
            AND (:correlationId IS NULL OR l.correlationId = :correlationId)
            AND (:endpoint IS NULL OR l.endpoint LIKE %:endpoint%)
            AND (:httpStatus IS NULL OR l.httpStatus = :httpStatus)
            AND (:startDate IS NULL OR l.createdAt >= :startDate)
            AND (:endDate IS NULL OR l.createdAt <= :endDate)
            AND (:keyword IS NULL OR LOWER(l.message) LIKE LOWER(CONCAT('%', :keyword, '%'))
                 OR LOWER(l.details) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<LogEntry> searchLogs(
            @Param("serviceName") String serviceName,
            @Param("level") LogLevel level,
            @Param("category") LogCategory category,
            @Param("authUserId") Long authUserId,
            @Param("traceId") String traceId,
            @Param("correlationId") String correlationId,
            @Param("endpoint") String endpoint,
            @Param("httpStatus") Integer httpStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    /**
     * Count logs for a specific service within a date range.
     */
    @Query("""
            SELECT COUNT(l) FROM LogEntry l
            WHERE l.serviceName = :serviceName
            AND l.createdAt BETWEEN :start AND :end
            """)
    Long countByServiceNameAndCreatedAtBetween(
            @Param("serviceName") String serviceName,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Get average duration for requests in a time range.
     */
    @Query("""
            SELECT COALESCE(AVG(l.durationMs), 0) FROM LogEntry l
            WHERE l.createdAt BETWEEN :start AND :end
            AND l.durationMs IS NOT NULL
            """)
    Double getAverageDurationMs(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
