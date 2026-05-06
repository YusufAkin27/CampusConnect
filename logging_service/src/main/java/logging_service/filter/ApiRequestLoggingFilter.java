package logging_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logging_service.dto.request.CreateApiRequestLogRequest;
import logging_service.service.ApiRequestLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filter that logs all incoming HTTP requests to the logging-service itself.
 *
 * IMPORTANT: Excludes log ingestion endpoints to prevent infinite loop:
 * The filter must NOT log calls to the log ingestion endpoints,
 * otherwise each log write would trigger another log write, causing infinite recursion.
 *
 * Excluded paths:
 * - /v1/api/logs/** (all ingestion endpoints)
 * - /actuator/**
 * - /swagger-ui/**
 * - /v3/api-docs/**
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiRequestLoggingFilter extends OncePerRequestFilter {

    private final ApiRequestLogService apiRequestLogService;

    private static final List<String> EXCLUDED_PREFIXES = Arrays.asList(
            "/v1/api/logs",       // Exclude all log ingestion to prevent infinite loop
            "/actuator",
            "/swagger-ui",
            "/v3/api-docs",
            "/webjars"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDED_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Log after the response is written
            long durationMs = System.currentTimeMillis() - startTime;
            logRequest(request, response, durationMs);
        }
    }

    private void logRequest(HttpServletRequest request, HttpServletResponse response, long durationMs) {
        try {
            String clientIp = getClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            String endpoint = request.getRequestURI();
            String httpMethod = request.getMethod();
            String queryString = request.getQueryString();
            int httpStatus = response.getStatus();
            boolean success = httpStatus >= 200 && httpStatus < 400;

            CreateApiRequestLogRequest logRequest = CreateApiRequestLogRequest.builder()
                    .serviceName("logging-service")
                    .httpMethod(httpMethod)
                    .endpoint(endpoint)
                    .queryString(queryString)
                    .httpStatus(httpStatus)
                    .durationMs(durationMs)
                    .clientIp(clientIp)
                    .userAgent(userAgent)
                    .success(success)
                    .build();

            apiRequestLogService.createApiRequestLog(logRequest);
        } catch (Exception e) {
            // Filter should never fail - just log and continue
            log.warn("ApiRequestLoggingFilter: failed to log request: {}", e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank()) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
