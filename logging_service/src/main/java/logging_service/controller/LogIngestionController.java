package logging_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import logging_service.common.response.DataResponseMessage;
import logging_service.dto.request.BatchLogEntryRequest;
import logging_service.dto.request.CreateLogEntryRequest;
import logging_service.dto.response.BatchLogResponse;
import logging_service.dto.response.LogEntryResponse;
import logging_service.service.LogIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/logs")
@RequiredArgsConstructor
@Tag(name = "Log Ingestion", description = "Endpoints for receiving logs from microservices")
public class LogIngestionController {

    private final LogIngestionService logIngestionService;

    @PostMapping
    @Operation(summary = "Create a single log entry")
    public ResponseEntity<DataResponseMessage<LogEntryResponse>> createLog(
            @Valid @RequestBody CreateLogEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(logIngestionService.createLog(request));
    }

    @PostMapping("/batch")
    @Operation(summary = "Create batch log entries (max 500)")
    public ResponseEntity<DataResponseMessage<BatchLogResponse>> createBatchLogs(
            @Valid @RequestBody BatchLogEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(logIngestionService.createBatchLogs(request));
    }

    @PostMapping("/async")
    @Operation(summary = "Async single log creation - returns immediately")
    public ResponseEntity<DataResponseMessage<Void>> createLogAsync(
            @Valid @RequestBody CreateLogEntryRequest request) {
        logIngestionService.createLogAsync(request);
        return ResponseEntity.accepted().body(DataResponseMessage.success("Log accepted for async processing", null));
    }

    @PostMapping("/batch/async")
    @Operation(summary = "Async batch log creation - returns immediately")
    public ResponseEntity<DataResponseMessage<Void>> createBatchLogsAsync(
            @Valid @RequestBody BatchLogEntryRequest request) {
        logIngestionService.createBatchLogsAsync(request.getLogs());
        return ResponseEntity.accepted().body(DataResponseMessage.success(
                "Batch of " + request.getLogs().size() + " logs accepted for async processing", null));
    }
}
