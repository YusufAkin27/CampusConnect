package media_service.service;

import media_service.common.response.DataResponseMessage;
import media_service.dto.request.ValidateMediaRequest;
import media_service.dto.response.MediaSummaryResponse;
import media_service.dto.response.ValidateMediaResponse;
import media_service.enums.MediaContext;

public interface MediaValidationService {

    /**
     * Validates a list of media IDs for use by another service.
     * Checks that all IDs are ACTIVE, belong to the correct context,
     * and optionally belong to the specified owner.
     *
     * Used primarily by post-service to validate mediaIds before post creation.
     */
    DataResponseMessage<ValidateMediaResponse> validateMedia(ValidateMediaRequest request);

    /**
     * Validates a single media file and returns its summary.
     */
    DataResponseMessage<MediaSummaryResponse> validateSingleMedia(
            Long mediaId,
            MediaContext expectedContext,
            Long ownerAuthUserId
    );
}
