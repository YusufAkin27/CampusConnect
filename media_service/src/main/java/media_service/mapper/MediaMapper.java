package media_service.mapper;

import media_service.dto.response.MediaFileResponse;
import media_service.dto.response.MediaSummaryResponse;
import media_service.dto.response.MediaUsageResponse;
import media_service.entity.MediaFile;
import media_service.entity.MediaUsage;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for converting entities to response DTOs.
 * MapStruct is intentionally NOT used.
 */
@Component
public class MediaMapper {

    public MediaFileResponse toMediaFileResponse(MediaFile mediaFile) {
        if (mediaFile == null) return null;
        return MediaFileResponse.builder()
                .id(mediaFile.getId())
                .ownerAuthUserId(mediaFile.getOwnerAuthUserId())
                .originalFilename(mediaFile.getOriginalFilename())
                .storedFilename(mediaFile.getStoredFilename())
                .storageKey(mediaFile.getStorageKey())
                .mediaUrl(mediaFile.getMediaUrl())
                .thumbnailUrl(mediaFile.getThumbnailUrl())
                .mediaType(mediaFile.getMediaType())
                .mediaContext(mediaFile.getMediaContext())
                .status(mediaFile.getStatus())
                .mimeType(mediaFile.getMimeType())
                .extension(mediaFile.getExtension())
                .fileSize(mediaFile.getFileSize())
                .width(mediaFile.getWidth())
                .height(mediaFile.getHeight())
                .duration(mediaFile.getDuration())
                .publicAccessible(mediaFile.getPublicAccessible())
                .createdAt(mediaFile.getCreatedAt())
                .updatedAt(mediaFile.getUpdatedAt())
                .build();
    }

    public MediaSummaryResponse toMediaSummaryResponse(MediaFile mediaFile) {
        if (mediaFile == null) return null;
        return MediaSummaryResponse.builder()
                .id(mediaFile.getId())
                .mediaUrl(mediaFile.getMediaUrl())
                .thumbnailUrl(mediaFile.getThumbnailUrl())
                .mediaType(mediaFile.getMediaType())
                .mediaContext(mediaFile.getMediaContext())
                .mimeType(mediaFile.getMimeType())
                .fileSize(mediaFile.getFileSize())
                .width(mediaFile.getWidth())
                .height(mediaFile.getHeight())
                .duration(mediaFile.getDuration())
                .build();
    }

    public MediaUsageResponse toMediaUsageResponse(MediaUsage mediaUsage) {
        if (mediaUsage == null) return null;
        return MediaUsageResponse.builder()
                .id(mediaUsage.getId())
                .mediaFileId(mediaUsage.getMediaFileId())
                .serviceName(mediaUsage.getServiceName())
                .targetType(mediaUsage.getTargetType())
                .targetId(mediaUsage.getTargetId())
                .authUserId(mediaUsage.getAuthUserId())
                .status(mediaUsage.getStatus())
                .createdAt(mediaUsage.getCreatedAt())
                .build();
    }
}
