package admin_service.service.impl;

import admin_service.client.MediaServiceClient;
import admin_service.enums.ActionType;
import admin_service.enums.TargetType;
import admin_service.security.AdminAction;
import admin_service.service.MediaAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaAdminServiceImpl implements MediaAdminService {

    private final MediaServiceClient mediaServiceClient;

    @Override
    public Map<String, Object> getAllMedia(int page, int size) {
        log.debug("Fetching all media: page={}, size={}", page, size);
        return mediaServiceClient.getAllMedia(page, size);
    }

    @Override
    public Map<String, Object> getMediaById(Long mediaId) {
        log.debug("Fetching media by id: {}", mediaId);
        return mediaServiceClient.getMediaById(mediaId);
    }

    @Override
    @AdminAction(actionType = ActionType.MEDIA_DELETED, targetType = TargetType.MEDIA)
    public Map<String, Object> deleteMedia(Long mediaId) {
        log.info("Deleting media: {}", mediaId);
        return mediaServiceClient.deleteMedia(mediaId);
    }

    @Override
    public Map<String, Object> getMediaByUser(Long userId, int page, int size) {
        return mediaServiceClient.getMediaByUser(userId, page, size);
    }

    @Override
    public Map<String, Object> getOrphanMedia() {
        return mediaServiceClient.getOrphanMedia();
    }

    @Override
    @AdminAction(actionType = ActionType.MEDIA_ORPHAN_CLEANUP, targetType = TargetType.MEDIA)
    public Map<String, Object> cleanupOrphanMedia() {
        log.info("Cleaning up orphan media files");
        return mediaServiceClient.cleanupOrphanMedia();
    }
}
