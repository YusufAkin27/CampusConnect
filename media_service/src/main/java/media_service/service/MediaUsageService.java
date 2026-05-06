package media_service.service;

import media_service.common.response.DataResponseMessage;
import media_service.common.response.ResponseMessage;
import media_service.dto.request.BatchRegisterMediaUsageRequest;
import media_service.dto.request.RegisterMediaUsageRequest;
import media_service.dto.response.MediaUsageResponse;

import java.util.List;

public interface MediaUsageService {

    /**
     * Registers a single media usage record.
     * Returns existing record if already registered (idempotent).
     */
    DataResponseMessage<MediaUsageResponse> registerUsage(
            Long authUserId,
            RegisterMediaUsageRequest request
    );

    /**
     * Registers multiple media usage records in a single call.
     */
    DataResponseMessage<List<MediaUsageResponse>> registerBatchUsage(
            Long authUserId,
            BatchRegisterMediaUsageRequest request
    );

    /**
     * Marks a specific usage record as REMOVED.
     */
    ResponseMessage removeUsage(
            Long authUserId,
            Long mediaId,
            String serviceName,
            String targetType,
            Long targetId
    );

    /**
     * Returns all active usage records for a specific target entity.
     */
    DataResponseMessage<List<MediaUsageResponse>> getUsageByTarget(
            String serviceName,
            String targetType,
            Long targetId
    );
}
