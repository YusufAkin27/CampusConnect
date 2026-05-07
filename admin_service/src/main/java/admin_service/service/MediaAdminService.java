package admin_service.service;

import java.util.Map;

public interface MediaAdminService {

    Map<String, Object> getAllMedia(int page, int size);

    Map<String, Object> getMediaById(Long mediaId);

    Map<String, Object> deleteMedia(Long mediaId);

    Map<String, Object> getMediaByUser(Long userId, int page, int size);

    Map<String, Object> getOrphanMedia();

    Map<String, Object> cleanupOrphanMedia();
}
