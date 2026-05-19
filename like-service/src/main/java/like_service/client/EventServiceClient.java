package like_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

/**
 * Event-service ile iletişim kurar.
 * Beğenilecek etkinliğin var olup olmadığını kontrol eder.
 */
@Slf4j
@Component
public class EventServiceClient {

    private final WebClient webClient;

    public EventServiceClient(WebClient.Builder webClientBuilder,
                              @Value("${event-service.base-url:http://event-service}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Event var mı kontrol eder.
     * Diğer serviste endpoint henüz yoksa fallback olarak true döner.
     *
     * @param eventId kontrol edilecek event ID
     * @return event varsa true
     */
    public boolean existsById(UUID eventId) {
        try {
            Boolean exists = webClient.get()
                    .uri("/internal/events/{eventId}/exists", eventId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            // TODO: event-service'te endpoint hazır olduğunda fallback kaldırılacak
            log.warn("Event-service'e ulaşılamadı veya endpoint henüz mevcut değil. eventId={}, hata: {}", eventId, e.getMessage());
            return true; // Fallback: event var kabul et
        }
    }
}
