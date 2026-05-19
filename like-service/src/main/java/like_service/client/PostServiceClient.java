package like_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

/**
 * Post-service ile iletişim kurar.
 * Beğenilecek postun var olup olmadığını kontrol eder.
 */
@Slf4j
@Component
public class PostServiceClient {

    private final WebClient webClient;

    public PostServiceClient(WebClient.Builder webClientBuilder,
                             @Value("${post-service.base-url:http://post-service}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Post var mı kontrol eder.
     * Diğer serviste endpoint henüz yoksa fallback olarak true döner.
     *
     * @param postId kontrol edilecek post ID
     * @return post varsa true
     */
    public boolean existsById(UUID postId) {
        try {
            Boolean exists = webClient.get()
                    .uri("/internal/posts/{postId}/exists", postId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            // TODO: post-service'te endpoint hazır olduğunda fallback kaldırılacak
            log.warn("Post-service'e ulaşılamadı veya endpoint henüz mevcut değil. postId={}, hata: {}", postId, e.getMessage());
            return true; // Fallback: post var kabul et
        }
    }
}
