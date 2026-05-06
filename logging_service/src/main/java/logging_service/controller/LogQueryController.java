package logging_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.response.LogEntryResponse;
import logging_service.dto.response.LogSummaryResponse;
import logging_service.enums.LogCategory;
import logging_service.enums.LogLevel;
import logging_service.enums.SortType;
import logging_service.service.LogQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/api/logs/query")
@RequiredArgsConstructor
@Tag(name = "Log Query", description = "Endpoints for searching and retrieving logs")
public class LogQueryController {

    private final LogQueryService logQueryService;

    @GetMapping("/{logId}")
    @Operation(summary = "Get log entry by ID")
    public ResponseEntity<DataResponseMessage<LogEntryResponse>> getLogById(@PathVariable Long logId) {
        return ResponseEntity.ok(logQueryService.getLogById(logId));
    }

    @GetMapping("/search")
    @Operation(summary = "Search logs with optional filters")
    public ResponseEntity<DataResponseMessage<PageResponse<LogSummaryResponse>>> searchLogs(
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) LogLevel level,
            @RequestParam(required = false) LogCategory category,
            @RequestParam(required = false) Long authUserId,
            @RequestParam(required = false) String traceId,
            @RequestParam(required = false) String correlationId,
            @RequestParam(required = false) String endpoint,
            @RequestParam(required = false) Integer httpStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "NEWEST") SortType sortType) {

        return ResponseEntity.ok(logQueryService.searchLogs(
                serviceName, level, category, authUserId, traceId, correlationId,
                endpoint, httpStatus, startDate, endDate, keyword, page, size, sortType));
    }

    @GetMapping("/service/{serviceName}")
    @Operation(summary = "Get logs by service name")
    public ResponseEntity<DataResponseMessage<PageResponse<LogSummaryResponse>>> getLogsByService(
            @PathVariable String serviceName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(logQueryService.getLogsByService(serviceName, page, size));
    }

    @GetMapping("/trace/{traceId}")
    @Operation(summary = "Get logs by trace ID")
    public ResponseEntity<DataResponseMessage<PageResponse<LogSummaryResponse>>> getLogsByTraceId(
            @PathVariable String traceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(logQueryService.getLogsByTraceId(traceId, page, size));
    }

    @GetMapping("/errors")
    @Operation(summary = "Get ERROR and FATAL level logs")
    public ResponseEntity<DataResponseMessage<PageResponse<LogSummaryResponse>>> getErrorLevelLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(logQueryService.getErrorLevelLogs(page, size));
    }
}
