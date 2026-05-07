package admin_service.controller;

import admin_service.common.response.DataResponseMessage;
import admin_service.dto.response.DashboardSummaryResponse;
import admin_service.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard & Statistics", description = "Admin panel dashboard statistics")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('SYSTEM_MONITOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Get dashboard summary")
    public ResponseEntity<DataResponseMessage<DashboardSummaryResponse>> getSummary() {
        return ResponseEntity.ok(DataResponseMessage.success("Dashboard summary.", dashboardService.getDashboardSummary()));
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    @Operation(summary = "Get user statistics")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getUserStats() {
        return ResponseEntity.ok(DataResponseMessage.success("User stats.", dashboardService.getUserStats()));
    }

    @GetMapping("/posts")
    @PreAuthorize("hasAuthority('POST_VIEW')")
    @Operation(summary = "Get post statistics")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getPostStats() {
        return ResponseEntity.ok(DataResponseMessage.success("Post stats.", dashboardService.getPostStats()));
    }

    @GetMapping("/reports")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    @Operation(summary = "Get report statistics")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getReportStats() {
        return ResponseEntity.ok(DataResponseMessage.success("Report stats.", dashboardService.getReportStats()));
    }

    @GetMapping("/media")
    @PreAuthorize("hasAuthority('MEDIA_VIEW')")
    @Operation(summary = "Get media statistics")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getMediaStats() {
        return ResponseEntity.ok(DataResponseMessage.success("Media stats.", dashboardService.getMediaStats()));
    }
}
