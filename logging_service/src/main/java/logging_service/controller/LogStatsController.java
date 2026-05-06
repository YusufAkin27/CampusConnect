package logging_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import logging_service.common.response.DataResponseMessage;
import logging_service.dto.response.DailyLogStatsResponse;
import logging_service.dto.response.LogStatsResponse;
import logging_service.dto.response.ServiceLogStatsResponse;
import logging_service.service.LogStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/api/logs/stats")
@RequiredArgsConstructor
@Tag(name = "Log Statistics", description = "Endpoints for log statistics and analytics")
public class LogStatsController {

    private final LogStatsService logStatsService;

    @GetMapping("/general")
    @Operation(summary = "Get general log statistics for a time period")
    public ResponseEntity<DataResponseMessage<LogStatsResponse>> getGeneralStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(logStatsService.getGeneralStats(startDate, endDate));
    }

    @GetMapping("/services")
    @Operation(summary = "Get log statistics grouped by service")
    public ResponseEntity<DataResponseMessage<List<ServiceLogStatsResponse>>> getStatsByService(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(logStatsService.getStatsByService(startDate, endDate));
    }

    @GetMapping("/daily")
    @Operation(summary = "Get daily log statistics")
    public ResponseEntity<DataResponseMessage<List<DailyLogStatsResponse>>> getDailyStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(logStatsService.getDailyStats(startDate, endDate));
    }
}
