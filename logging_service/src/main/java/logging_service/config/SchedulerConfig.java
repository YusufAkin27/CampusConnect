package logging_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Scheduler configuration.
 * Enables Spring's @Scheduled task execution.
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
    // Scheduling is enabled via @EnableScheduling
    // Scheduled tasks are defined in LogCleanupScheduler
}
