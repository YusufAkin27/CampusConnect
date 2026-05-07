package admin_service.service;

import admin_service.dto.response.ServiceHealthResponse;

import java.util.List;
import java.util.Map;

public interface SystemHealthService {

    List<ServiceHealthResponse> getAllServicesHealth();

    ServiceHealthResponse getServiceHealth(String serviceName);

    Map<String, Object> getOverallSystemHealth();

    Map<String, Object> getConsulServices();
}
