package logging_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import logging_service.common.response.DataResponseMessage;
import logging_service.common.response.PageResponse;
import logging_service.dto.request.CreateApiRequestLogRequest;
import logging_service.dto.response.ApiRequestLogResponse;
import logging_service.service.ApiRequestLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/logs/api-requests")
@RequiredArgsConstructor
@Tag(name = "API Request Logs", description = "Endpoints for API request log management")
public class ApiRequestLogController {

    private final ApiRequestLogService apiRequestLogService;

    @PostMapping
    @Operation(summary = "Create an API request log entry")
    public ResponseEntity<DataResponseMessage<ApiRequestLogResponse>> createApiRequestLog(
            @Valid @RequestBody CreateApiRequestLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(apiRequestLogService.createApiRequestLog(request));
    }

    @GetMapping("/service/{serviceName}")
    @Operation(summary = "Get API request logs for a service")
    public ResponseEntity<DataResponseMessage<PageResponse<ApiRequestLogResponse>>> getApiRequestsByService(
            @PathVariable String serviceName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(apiRequestLogService.getApiRequestsByService(serviceName, page, size));
    }

    @GetMapping("/slow")
    @Operation(summary = "Get slow requests above a duration threshold")
    public ResponseEntity<DataResponseMessage<PageResponse<ApiRequestLogResponse>>> getSlowRequests(
            @RequestParam(defaultValue = "1000") Long minDurationMs,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(apiRequestLogService.getSlowRequests(minDurationMs, page, size));
    }
}
