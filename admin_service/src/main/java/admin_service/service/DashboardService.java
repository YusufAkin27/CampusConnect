package admin_service.service;

import admin_service.dto.response.DashboardSummaryResponse;

import java.util.Map;

public interface DashboardService {

    DashboardSummaryResponse getDashboardSummary();

    Map<String, Object> getUserStats();

    Map<String, Object> getPostStats();

    Map<String, Object> getReportStats();

    Map<String, Object> getMediaStats();
}
