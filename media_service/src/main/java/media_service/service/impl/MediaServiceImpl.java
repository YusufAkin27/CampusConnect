package media_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import media_service.common.response.DataResponseMessage;
import media_service.common.response.PageResponse;
import media_service.common.response.ResponseMessage;
import media_service.dto.request.UpdateMediaStatusRequest;
import media_service.dto.response.MediaFileResponse;
import media_service.entity.MediaFile;
import media_service.enums.MediaContext;
import media_service.enums.MediaStatus;
import media_service.enums.MediaType;
import media_service.enums.SortType;
import media_service.exception.MediaNotFoundException;
import media_service.exception.MediaUploadException;
import media_service.exception.UnauthorizedMediaAccessException;
import media_service.mapper.MediaMapper;
import media_service.repository.MediaFileRepository;
import media_service.repository.MediaUsageRepository;
import media_service.service.MediaService;
import media_service.service.StorageService;
import media_service.enums.MediaUsageStatus;
import media_service.util.ChecksumUtil;
import media_service.util.FileNameSanitizer;
import media_service.util.ImageMetadataExtractor;
import media_service.util.MediaValidationUtil;
import media_service.util.PageResponseConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaFileRepository mediaFileRepository;
    private final MediaUsageRepository mediaUsageRepository;
    private final StorageService storageService;
    private final MediaValidationUtil mediaValidationUtil;
    private final ImageMetadataExtractor imageMetadataExtractor;
    private final ChecksumUtil checksumUtil;
    private final MediaMapper mediaMapper;
    private final PageResponseConverter pageResponseConverter;

    @Override
    @Transactional
    public DataResponseMessage<MediaFileResponse> uploadMedia(
            Long authUserId,
            MultipartFile file,
            MediaContext mediaContext,
            Boolean publicAccessible) {

        // Validations
        if (authUserId == null) throw new MediaUploadException("Auth user ID is required.");
        if (mediaContext == null) throw new MediaUploadException("Media context is required.");

        mediaValidationUtil.validateFile(file);

        String mimeType = file.getContentType();
        MediaType mediaType = mediaValidationUtil.detectMediaType(mimeType);

        // Sanitize and generate filenames
        String originalFilename = FileNameSanitizer.sanitizeOriginalFilename(file.getOriginalFilename());
        String extension = FileNameSanitizer.getExtension(file.getOriginalFilename());
        String storedFilename = FileNameSanitizer.generateStoredFilename(file.getOriginalFilename());

        // Store the file
        String storageKey = storageService.storeFile(file, authUserId, mediaContext, storedFilename);
        String mediaUrl = storageService.buildMediaUrl(storageKey);

        // Extract image dimensions if applicable
        Integer width = null;
        Integer height = null;
        if (mediaType == MediaType.IMAGE || mediaType == MediaType.GIF) {
            int[] dims = imageMetadataExtractor.extractDimensions(file);
            if (dims != null) {
                width = dims[0];
                height = dims[1];
            }
        }

        // Duration for video/audio
        // TODO: Implement video duration extraction using JavaCV or FFmpeg wrapper
        Double duration = null;

        // Thumbnail URL
        // TODO: Implement actual thumbnail generation (image resize / video frame extraction)
        String thumbnailUrl;
        if (mediaType == MediaType.IMAGE || mediaType == MediaType.GIF) {
            thumbnailUrl = mediaUrl; // For v1: thumbnail = original URL
        } else {
            thumbnailUrl = null; // For video/audio: no thumbnail in v1
        }

        // Optional: calculate checksum (TODO: use for duplicate detection)
        String checksum = checksumUtil.calculateChecksum(file);

        MediaFile mediaFile = MediaFile.builder()
                .ownerAuthUserId(authUserId)
                .originalFilename(originalFilename)
                .storedFilename(storedFilename)
                .storageKey(storageKey)
                .mediaUrl(mediaUrl)
                .thumbnailUrl(thumbnailUrl)
                .mediaType(mediaType)
                .mediaContext(mediaContext)
                .status(MediaStatus.ACTIVE)
                .mimeType(mimeType)
                .extension(extension)
                .fileSize(file.getSize())
                .width(width)
                .height(height)
                .duration(duration)
                .checksum(checksum)
                .publicAccessible(publicAccessible != null ? publicAccessible : true)
                .build();

        mediaFile = mediaFileRepository.save(mediaFile);
        log.info("Media uploaded: id={}, storageKey={}, type={}, context={}", 
                  mediaFile.getId(), storageKey, mediaType, mediaContext);

        return DataResponseMessage.success("Media uploaded successfully.", mediaMapper.toMediaFileResponse(mediaFile));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<MediaFileResponse> getMediaById(Long mediaId) {
        MediaFile mediaFile = mediaFileRepository.findByIdAndStatus(mediaId, MediaStatus.ACTIVE)
                .orElseThrow(() -> new MediaNotFoundException(mediaId));
        return DataResponseMessage.success("Media retrieved successfully.", mediaMapper.toMediaFileResponse(mediaFile));
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponseMessage<PageResponse<MediaFileResponse>> getMyMedia(
            Long authUserId,
            MediaContext mediaContext,
            MediaType mediaType,
            int page,
            int size,
            SortType sortType) {

        Pageable pageable = PageRequest.of(page, size, buildSort(sortType));
        Page<MediaFile> result = mediaFileRepository.searchMedia(
                authUserId, mediaContext, mediaType, MediaStatus.ACTIVE, pageable);
        Page<MediaFileResponse> mapped = result.map(mediaMapper::toMediaFileResponse);
        return DataResponseMessage.success("Media list retrieved.", pageResponseConverter.toPageResponse(mapped));
    }

    @Override
    @Transactional
    public ResponseMessage deleteMedia(Long authUserId, Long mediaId) {
        MediaFile mediaFile = mediaFileRepository.findByIdAndStatus(mediaId, MediaStatus.ACTIVE)
                .orElseThrow(() -> new MediaNotFoundException(mediaId));

        if (!mediaFile.getOwnerAuthUserId().equals(authUserId)) {
            throw new UnauthorizedMediaAccessException("Only the owner can delete this media file.");
        }

        // Soft delete - do NOT perform physical file deletion here.
        // Physical cleanup is handled by OrphanMediaCleanupScheduler or a separate admin job.
        // This is intentional for safety: active usages should not result in broken URLs immediately.
        mediaFile.softDelete();
        mediaFileRepository.save(mediaFile);

        log.info("Media soft-deleted: id={}, authUserId={}", mediaId, authUserId);
        return ResponseMessage.success("Media deleted successfully.");
    }

    @Override
    @Transactional
    public DataResponseMessage<MediaFileResponse> updateMediaStatus(Long mediaId, UpdateMediaStatusRequest request) {
        MediaFile mediaFile = mediaFileRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException(mediaId));
        mediaFile.setStatus(request.getStatus());
        if (request.getStatus() == MediaStatus.DELETED) {
            mediaFile.setDeletedAt(java.time.LocalDateTime.now());
        }
        mediaFile = mediaFileRepository.save(mediaFile);
        return DataResponseMessage.success("Media status updated.", mediaMapper.toMediaFileResponse(mediaFile));
    }

    // ==================== Helpers ====================

    private Sort buildSort(SortType sortType) {
        if (sortType == null) return Sort.by(Sort.Direction.DESC, "createdAt");
        return switch (sortType) {
            case NEWEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case OLDEST -> Sort.by(Sort.Direction.ASC, "createdAt");
            case SIZE_DESC -> Sort.by(Sort.Direction.DESC, "fileSize");
            case SIZE_ASC -> Sort.by(Sort.Direction.ASC, "fileSize");
        };
    }
}
