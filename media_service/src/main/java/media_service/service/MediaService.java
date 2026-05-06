package media_service.service;

import media_service.common.response.DataResponseMessage;
import media_service.common.response.PageResponse;
import media_service.common.response.ResponseMessage;
import media_service.dto.request.UpdateMediaStatusRequest;
import media_service.dto.response.MediaFileResponse;
import media_service.enums.MediaContext;
import media_service.enums.MediaType;
import media_service.enums.SortType;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    /**
     * Uploads a media file, validates it, stores it, and persists the metadata.
     */
    DataResponseMessage<MediaFileResponse> uploadMedia(
            Long authUserId,
            MultipartFile file,
            MediaContext mediaContext,
            Boolean publicAccessible
    );

    /**
     * Returns the full details of a media file by ID.
     */
    DataResponseMessage<MediaFileResponse> getMediaById(Long mediaId);

    /**
     * Returns a paginated list of media files uploaded by the current user.
     * Supports optional filtering by context and type.
     */
    DataResponseMessage<PageResponse<MediaFileResponse>> getMyMedia(
            Long authUserId,
            MediaContext mediaContext,
            MediaType mediaType,
            int page,
            int size,
            SortType sortType
    );

    /**
     * Soft-deletes a media file. Only the owner can delete.
     */
    ResponseMessage deleteMedia(Long authUserId, Long mediaId);

    /**
     * Admin endpoint to update media status directly.
     */
    DataResponseMessage<MediaFileResponse> updateMediaStatus(
            Long mediaId,
            UpdateMediaStatusRequest request
    );
}
