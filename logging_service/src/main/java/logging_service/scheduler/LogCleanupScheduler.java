package logging_service.scheduler;

import logging_service.service.LogRetentionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled job for automatic log cleanup based on retention policies.
 *
 * Runs every night at 03:00 AM (low traffic period).
 * Can be disabled via app.log-cleanup.enabled=false property.
 *
 * TODO: For very large datasets (millions of rows), consider:
 * 1. PostgreSQL table partitioning (PARTITION BY RANGE on created_at)
 *    to enable DROP PARTITION instead of DELETE operations.
 * 2. Running cleanup in smaller batches to avoid long-running transactions.
 * 3. Using pg_partman extension for automated partition management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LogCleanupScheduler {

    private final LogRetentionService logRetentionService;

    @Value("${app.log-cleanup.enabled:true}")
    private boolean cleanupEnabled;

    /**
     * Runs every night at 03:00 AM server time.
     * Cleans up logs based on configured retention policies.
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void scheduledLogCleanup() {
        if (!cleanupEnabled) {
            log.debug("Log cleanup is disabled (app.log-cleanup.enabled=false). Skipping.");
            return;
        }

        log.info("Starting scheduled log cleanup at 03:00 AM...");
        try {
            logRetentionService.cleanupOldLogs();
            log.info("Scheduled log cleanup completed successfully.");
        } catch (Exception e) {
            // System should NOT crash even if cleanup fails
            log.error("Scheduled log cleanup failed: {}. System continues operating normally.", e.getMessage(), e);
        }
    }
}
