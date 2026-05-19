package like_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

/**
 * Story-service ile iletişim kurar.
 * Beğenilecek hikayenin var olup olmadığını kontrol eder.
 */
@Slf4j
@Component
public class StoryServiceClient {

    private final WebClient webClient;

    public StoryServiceClient(WebClient.Builder webClientBuilder,
                              @Value("${story-service.base-url:http://story-service}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Story var mı kontrol eder.
     * Diğer serviste endpoint henüz yoksa fallback olarak true döner.
     *
     * @param storyId kontrol edilecek story ID
     * @return story varsa true
     */
    public boolean existsById(UUID storyId) {
        try {
            Boolean exists = webClient.get()
                    .uri("/internal/stories/{storyId}/exists", storyId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            // TODO: story-service'te endpoint hazır olduğunda fallback kaldırılacak
            log.warn("Story-service'e ulaşılamadı veya endpoint henüz mevcut değil. storyId={}, hata: {}", storyId, e.getMessage());
            return true; // Fallback: story var kabul et
        }
    }
}
