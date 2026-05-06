package logging_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration for logging operations.
 * Provides a dedicated thread pool for async log processing.
 * This prevents log operations from blocking the main request threads.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Dedicated thread pool for async logging operations.
     * Tuned for high-throughput log ingestion:
     * - corePoolSize: 4 threads always ready
     * - maxPoolSize: 16 threads under peak load
     * - queueCapacity: 10000 logs can queue before rejection
     */
    @Bean(name = "loggingAsyncExecutor")
    public Executor loggingAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("logging-async-");
        executor.setRejectedExecutionHandler((r, ex) -> {
            // If queue is full, log warning but don't fail
            System.err.println("[WARNING] Logging async queue full - log entry dropped. Consider increasing queueCapacity.");
        });
        executor.initialize();
        return executor;
    }
}
