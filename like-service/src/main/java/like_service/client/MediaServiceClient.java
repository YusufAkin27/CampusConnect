package like_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

/**
 * Media-service ile iletişim kurar.
 * Beğenilecek medyanın var olup olmadığını kontrol eder.
 */
@Slf4j
@Component
public class MediaServiceClient {

    private final WebClient webClient;

    public MediaServiceClient(WebClient.Builder webClientBuilder,
                              @Value("${media-service.base-url:http://media-service}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Media var mı kontrol eder.
     * Diğer serviste endpoint henüz yoksa fallback olarak true döner.
     *
     * @param mediaId kontrol edilecek media ID
     * @return media varsa true
     */
    public boolean existsById(UUID mediaId) {
        try {
            Boolean exists = webClient.get()
                    .uri("/internal/media/{mediaId}/exists", mediaId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            // TODO: media-service'te endpoint hazır olduğunda fallback kaldırılacak
            log.warn("Media-service'e ulaşılamadı veya endpoint henüz mevcut değil. mediaId={}, hata: {}", mediaId, e.getMessage());
            return true; // Fallback: media var kabul et
        }
    }
}
