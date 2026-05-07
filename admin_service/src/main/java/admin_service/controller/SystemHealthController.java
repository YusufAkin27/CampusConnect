package admin_service.controller;

import admin_service.common.response.DataResponseMessage;
import admin_service.dto.response.ServiceHealthResponse;
import admin_service.service.SystemHealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/admin/system")
@RequiredArgsConstructor
@Tag(name = "System Health Monitoring", description = "Monitor service health via Consul and Actuator")
public class SystemHealthController {

    private final SystemHealthService systemHealthService;

    @GetMapping("/services")
    @PreAuthorize("hasAuthority('SYSTEM_MONITOR')")
    @Operation(summary = "Get health status of all services")
    public ResponseEntity<DataResponseMessage<List<ServiceHealthResponse>>> getAllServices() {
        return ResponseEntity.ok(DataResponseMessage.success("Services health.", systemHealthService.getAllServicesHealth()));
    }

    @GetMapping("/services/{serviceName}")
    @PreAuthorize("hasAuthority('SYSTEM_MONITOR')")
    @Operation(summary = "Get health status of a specific service")
    public ResponseEntity<DataResponseMessage<ServiceHealthResponse>> getService(@PathVariable String serviceName) {
        return ResponseEntity.ok(DataResponseMessage.success("Service health.", systemHealthService.getServiceHealth(serviceName)));
    }

    @GetMapping("/health")
    @PreAuthorize("hasAuthority('SYSTEM_MONITOR')")
    @Operation(summary = "Get overall system health")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getSystemHealth() {
        return ResponseEntity.ok(DataResponseMessage.success("System health.", systemHealthService.getOverallSystemHealth()));
    }

    @GetMapping("/consul/services")
    @PreAuthorize("hasAuthority('SYSTEM_MONITOR')")
    @Operation(summary = "Get Consul registered services")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getConsulServices() {
        return ResponseEntity.ok(DataResponseMessage.success("Consul services.", systemHealthService.getConsulServices()));
    }
}
