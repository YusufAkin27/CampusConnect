package admin_service.service.impl;

import admin_service.dto.response.ServiceHealthResponse;
import admin_service.service.SystemHealthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SystemHealthServiceImpl implements SystemHealthService {

    private final DiscoveryClient discoveryClient;

    private static final List<String> MONITORED_SERVICES = List.of(
            "api-gateway", "auth-service", "user-service", "post-service",
            "media-service", "friend-service", "notification-service",
            "logging-service", "event-service", "admin-service"
    );

    @Override
    public List<ServiceHealthResponse> getAllServicesHealth() {
        return MONITORED_SERVICES.stream()
                .map(this::getServiceHealth)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceHealthResponse getServiceHealth(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

        if (instances.isEmpty()) {
            return ServiceHealthResponse.builder()
                    .serviceName(serviceName)
                    .status("DOWN")
                    .lastCheckedAt(LocalDateTime.now())
                    .build();
        }

        ServiceInstance instance = instances.get(0);
        String healthUrl = instance.getUri() + "/actuator/health";

        ServiceHealthResponse.ServiceHealthResponseBuilder builder = ServiceHealthResponse.builder()
                .serviceName(serviceName)
                .host(instance.getHost())
                .port(instance.getPort())
                .healthCheckUrl(healthUrl)
                .lastCheckedAt(LocalDateTime.now());

        try {
            RestTemplate restTemplate = new RestTemplate();
            long start = System.currentTimeMillis();
            restTemplate.getForObject(healthUrl, Map.class);
            long elapsed = System.currentTimeMillis() - start;
            builder.status("UP").responseTimeMs(elapsed);
        } catch (Exception e) {
            log.warn("Health check failed for {}: {}", serviceName, e.getMessage());
            builder.status("DOWN").responseTimeMs(null);
        }

        return builder.build();
    }

    @Override
    public Map<String, Object> getOverallSystemHealth() {
        List<ServiceHealthResponse> health = getAllServicesHealth();
        long upCount = health.stream().filter(s -> "UP".equals(s.getStatus())).count();
        Map<String, Object> result = new HashMap<>();
        result.put("totalServices", health.size());
        result.put("healthyServices", upCount);
        result.put("unhealthyServices", health.size() - upCount);
        result.put("overallStatus", upCount == health.size() ? "HEALTHY" : "DEGRADED");
        result.put("services", health);
        return result;
    }

    @Override
    public Map<String, Object> getConsulServices() {
        List<String> services = discoveryClient.getServices();
        Map<String, Object> result = new HashMap<>();
        result.put("registeredServices", services);
        result.put("totalCount", services.size());
        Map<String, Integer> instanceCounts = new HashMap<>();
        for (String svc : services) {
            instanceCounts.put(svc, discoveryClient.getInstances(svc).size());
        }
        result.put("instanceCounts", instanceCounts);
        return result;
    }
}
