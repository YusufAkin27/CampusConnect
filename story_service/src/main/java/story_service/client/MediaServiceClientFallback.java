package story_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import story_service.dto.response.MediaValidationResponse;

/**
 * Fallback implementation for MediaServiceClient.
 * Returns safe defaults when media-service is unavailable.
 */
@Slf4j
@Component
public class MediaServiceClientFallback implements MediaServiceClient {

    @Override
    public MediaValidationResponse validateMedia(Long mediaId, Long userId) {
        log.warn("Media service unavailable. Fallback triggered for media validation. mediaId={}, userId={}", mediaId, userId);
        return MediaValidationResponse.builder()
            .mediaId(mediaId)
            .valid(false)
            .message("Media service is currently unavailable. Please try again later.")
            .build();
    }

    @Override
    public void registerMediaUsage(Long mediaId, String usageType, String referenceId) {
        log.warn("Media service unavailable. Fallback triggered for usage registration. mediaId={}, usageType={}", mediaId, usageType);
        // Silently fail - usage registration is not critical for story creation
    }
}
