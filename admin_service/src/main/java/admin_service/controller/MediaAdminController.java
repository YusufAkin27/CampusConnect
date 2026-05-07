package admin_service.controller;

import admin_service.common.response.DataResponseMessage;
import admin_service.service.MediaAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/admin/media")
@RequiredArgsConstructor
@Tag(name = "Media Administration", description = "Manage platform media files")
public class MediaAdminController {

    private final MediaAdminService mediaAdminService;

    @GetMapping
    @PreAuthorize("hasAuthority('MEDIA_VIEW')")
    @Operation(summary = "List all media files")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getAllMedia(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(DataResponseMessage.success("Media retrieved.", mediaAdminService.getAllMedia(page, size)));
    }

    @GetMapping("/{mediaId}")
    @PreAuthorize("hasAuthority('MEDIA_VIEW')")
    @Operation(summary = "Get media details")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getMediaById(@PathVariable Long mediaId) {
        return ResponseEntity.ok(DataResponseMessage.success("Media retrieved.", mediaAdminService.getMediaById(mediaId)));
    }

    @DeleteMapping("/{mediaId}")
    @PreAuthorize("hasAuthority('MEDIA_DELETE')")
    @Operation(summary = "Delete a media file")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> deleteMedia(@PathVariable Long mediaId) {
        return ResponseEntity.ok(DataResponseMessage.success("Media deleted.", mediaAdminService.deleteMedia(mediaId)));
    }

    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAuthority('MEDIA_VIEW')")
    @Operation(summary = "Get media by user")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getMediaByUser(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(DataResponseMessage.success("User media.", mediaAdminService.getMediaByUser(userId, page, size)));
    }

    @GetMapping("/orphan")
    @PreAuthorize("hasAuthority('MEDIA_VIEW')")
    @Operation(summary = "Get orphan media files")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> getOrphanMedia() {
        return ResponseEntity.ok(DataResponseMessage.success("Orphan media.", mediaAdminService.getOrphanMedia()));
    }

    @DeleteMapping("/orphan/cleanup")
    @PreAuthorize("hasAuthority('MEDIA_DELETE')")
    @Operation(summary = "Cleanup orphan media files")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> cleanupOrphan() {
        return ResponseEntity.ok(DataResponseMessage.success("Orphan media cleanup completed.", mediaAdminService.cleanupOrphanMedia()));
    }
}
