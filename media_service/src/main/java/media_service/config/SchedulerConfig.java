package media_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables Spring's scheduled task execution.
 * Required for the OrphanMediaCleanupScheduler.
 *
 * Scheduling is also configured at the application level via @EnableScheduling
 * on the main class. This config class provides an explicit, discoverable configuration.
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
    // Scheduler is enabled; no additional bean configuration required.
    // Customize thread pool settings here if needed for production workloads.
}
