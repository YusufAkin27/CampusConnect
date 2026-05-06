package logging_service.repository;

import logging_service.entity.ApiRequestLog;
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
public interface ApiRequestLogRepository extends JpaRepository<ApiRequestLog, Long> {

    Page<ApiRequestLog> findByServiceName(String serviceName, Pageable pageable);

    Page<ApiRequestLog> findByEndpointContainingIgnoreCase(String endpoint, Pageable pageable);

    Page<ApiRequestLog> findByHttpStatus(Integer httpStatus, Pageable pageable);

    Long countByServiceName(String serviceName);

    Long countByHttpStatusGreaterThanEqual(Integer status);

    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Long countByServiceNameAndCreatedAtBetween(String serviceName, LocalDateTime start, LocalDateTime end);

    /**
     * Finds slow requests where duration exceeds the given threshold.
     */
    @Query("""
            SELECT a FROM ApiRequestLog a
            WHERE (:serviceName IS NULL OR a.serviceName = :serviceName)
            AND a.durationMs >= :minDurationMs
            ORDER BY a.durationMs DESC
            """)
    Page<ApiRequestLog> findSlowRequests(
            @Param("serviceName") String serviceName,
            @Param("minDurationMs") Long minDurationMs,
            Pageable pageable
    );

    /**
     * Calculates average duration for a specific service.
     */
    @Query("""
            SELECT COALESCE(AVG(a.durationMs), 0) FROM ApiRequestLog a
            WHERE a.serviceName = :serviceName
            AND a.durationMs IS NOT NULL
            """)
    Double getAverageDurationByService(@Param("serviceName") String serviceName);

    @Modifying
    @Transactional
    void deleteByCreatedAtBefore(LocalDateTime date);
}
