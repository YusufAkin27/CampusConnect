package story_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import story_service.dto.response.MediaValidationResponse;

/**
 * Feign client for communicating with the media-service.
 * Handles media validation, detail retrieval, and usage registration.
 * Uses Consul service discovery to resolve the media-service instance.
 */
@FeignClient(
    name = "${media.service.name}",
    fallback = MediaServiceClientFallback.class
)
public interface MediaServiceClient {

    /**
     * Validates a media by its ID and returns detailed validation info.
     * Checks: media exists, owner matches, media type is suitable for stories.
     *
     * @param mediaId the media ID to validate
     * @param userId  the user ID who is trying to use this media
     * @return MediaValidationResponse with validity, owner, URL, and type info
     */
    @GetMapping("/v1/api/media/internal/{mediaId}/validate")
    MediaValidationResponse validateMedia(
        @PathVariable("mediaId") Long mediaId,
        @RequestParam("userId") Long userId
    );

    /**
     * Registers media usage for a story.
     * Notifies media-service that this media is being used in a story.
     *
     * @param mediaId   the media ID
     * @param usageType the usage type (e.g., "STORY")
     * @param referenceId the story reference ID as string (UUID)
     */
    @PostMapping("/v1/api/media/internal/{mediaId}/usage")
    void registerMediaUsage(
        @PathVariable("mediaId") Long mediaId,
        @RequestParam("usageType") String usageType,
        @RequestParam("referenceId") String referenceId
    );
}
