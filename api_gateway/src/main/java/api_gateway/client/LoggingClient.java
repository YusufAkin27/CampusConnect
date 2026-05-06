package api_gateway.client;

import api_gateway.dto.GatewayLogRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class LoggingClient {

    private final WebClient webClient = WebClient.builder().build();

    public void sendGatewayLog(String baseUrl, GatewayLogRequest request) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return;
        }
        webClient.post()
            .uri(baseUrl + "/v1/api/logs/internal/events")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .toBodilessEntity()
            .doOnError(error -> log.warn("Logging service error", error))
            .subscribe();
    }
}
