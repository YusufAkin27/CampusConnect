package media_service.service;

import media_service.common.response.DataResponseMessage;
import media_service.dto.response.MediaStatsResponse;
import media_service.dto.response.StorageInfoResponse;

public interface MediaStatsService {

    /**
     * Returns aggregate statistics about media files.
     */
    DataResponseMessage<MediaStatsResponse> getStats();

    /**
     * Returns storage configuration information.
     */
    DataResponseMessage<StorageInfoResponse> getStorageInfo();
}
