package event_service.client;

import event_service.client.dto.LogEventRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${logging.service.name}")
public interface LoggingClient {

    @PostMapping("/v1/api/logs/internal/events")
    void logEvent(@RequestBody LogEventRequest request);
}
