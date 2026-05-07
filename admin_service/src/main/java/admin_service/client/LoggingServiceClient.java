package admin_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Feign client for communicating with logging-service.
 */
@FeignClient(name = "logging-service", path = "/v1/api/logs")
public interface LoggingServiceClient {

    @GetMapping("/admin/all")
    Map<String, Object> getAllLogs(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size);

    @GetMapping("/admin/by-service")
    Map<String, Object> getLogsByService(@RequestParam("serviceName") String serviceName,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size);

    @GetMapping("/admin/by-level")
    Map<String, Object> getLogsByLevel(@RequestParam("level") String level,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size);
}
