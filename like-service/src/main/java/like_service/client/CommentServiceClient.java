package like_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

/**
 * Comment-service ile iletişim kurar.
 * Beğenilecek yorumun var olup olmadığını kontrol eder.
 */
@Slf4j
@Component
public class CommentServiceClient {

    private final WebClient webClient;

    public CommentServiceClient(WebClient.Builder webClientBuilder,
                                @Value("${comment-service.base-url:http://comment-service}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Comment var mı kontrol eder.
     * Diğer serviste endpoint henüz yoksa fallback olarak true döner.
     *
     * @param commentId kontrol edilecek comment ID
     * @return comment varsa true
     */
    public boolean existsById(UUID commentId) {
        try {
            Boolean exists = webClient.get()
                    .uri("/internal/comments/{commentId}/exists", commentId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            // TODO: comment-service'te endpoint hazır olduğunda fallback kaldırılacak
            log.warn("Comment-service'e ulaşılamadı veya endpoint henüz mevcut değil. commentId={}, hata: {}", commentId, e.getMessage());
            return true; // Fallback: comment var kabul et
        }
    }
}
