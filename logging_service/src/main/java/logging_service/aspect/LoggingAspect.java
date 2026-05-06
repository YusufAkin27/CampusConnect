package logging_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * AOP aspect for logging service method execution times and errors.
 * Monitors the logging-service's own internal operations.
 *
 * This aspect helps detect performance issues within the logging service itself
 * (e.g., slow database writes, slow mapper operations).
 *
 * NOTE: This does NOT create new log entries to avoid recursion.
 * It uses SLF4J directly for internal diagnostic logging.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final long SLOW_METHOD_THRESHOLD_MS = 500;

    /**
     * Monitors execution time of all service implementations.
     * Logs a warning if execution takes longer than SLOW_METHOD_THRESHOLD_MS.
     */
    @Around("execution(* logging_service.service.impl.*.*(..))")
    public Object monitorServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            if (duration > SLOW_METHOD_THRESHOLD_MS) {
                log.warn("[SLOW_METHOD] {} took {}ms - exceeds threshold of {}ms",
                        methodName, duration, SLOW_METHOD_THRESHOLD_MS);
            } else {
                log.debug("[METHOD] {} completed in {}ms", methodName, duration);
            }

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("[METHOD_ERROR] {} failed after {}ms: {}", methodName, duration, e.getMessage());
            throw e;
        }
    }

    /**
     * Monitors execution time of all repository operations.
     * Logs a warning if database operation is slow.
     */
    @Around("execution(* logging_service.repository.*.*(..))")
    public Object monitorRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            if (duration > SLOW_METHOD_THRESHOLD_MS) {
                log.warn("[SLOW_DB] {} took {}ms - consider adding index or optimizing query",
                        methodName, duration);
            }

            return result;
        } catch (Exception e) {
            log.error("[DB_ERROR] {} failed: {}", methodName, e.getMessage());
            throw e;
        }
    }
}
