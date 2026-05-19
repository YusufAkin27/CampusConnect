package like_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import like_service.config.CurrentUserResolver;
import like_service.dto.request.BulkLikeCountRequest;
import like_service.dto.request.LikeRequest;
import like_service.dto.response.*;
import like_service.entity.LikeTargetType;
import like_service.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/api/likes")
@RequiredArgsConstructor
@Tag(name = "Like API", description = "İçerik beğenme ve beğeni yönetimi işlemleri")
public class LikeController {

    private final LikeService likeService;
    private final CurrentUserResolver currentUserResolver;

    @PostMapping
    @Operation(summary = "İçerik beğen", description = "Belirtilen içeriği beğenir. Aynı içerik tekrar beğenilirse idempotent davranır.")
    public ResponseEntity<LikeStatusResponse> like(@Valid @RequestBody LikeRequest request) {
        UUID currentUserId = currentUserResolver.getCurrentUserId();
        LikeStatusResponse response = likeService.like(request, currentUserId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(summary = "İçerik beğenisini kaldır", description = "Belirtilen içeriğin beğenisini kaldırır (soft remove).")
    public ResponseEntity<LikeStatusResponse> unlike(@Valid @RequestBody LikeRequest request) {
        UUID currentUserId = currentUserResolver.getCurrentUserId();
        LikeStatusResponse response = likeService.unlike(request, currentUserId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/toggle")
    @Operation(summary = "Toggle like", description = "Beğenmişse kaldırır, beğenmemişse beğenir.")
    public ResponseEntity<LikeStatusResponse> toggle(@Valid @RequestBody LikeRequest request) {
        UUID currentUserId = currentUserResolver.getCurrentUserId();
        LikeStatusResponse response = likeService.toggle(request, currentUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    @Operation(summary = "Beğeni sayısını getir", description = "Belirtilen içeriğin aktif beğeni sayısını döner.")
    public ResponseEntity<LikeCountResponse> getLikeCount(
            @Parameter(description = "Hedef içerik ID") @RequestParam UUID targetId,
            @Parameter(description = "Hedef içerik türü") @RequestParam LikeTargetType targetType) {
        LikeCountResponse response = likeService.getLikeCount(targetId, targetType);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    @Operation(summary = "Beğeni durumunu getir", description = "Mevcut kullanıcının belirtilen içeriği beğenip beğenmediğini ve beğeni sayısını döner.")
    public ResponseEntity<LikeStatusResponse> getLikeStatus(
            @Parameter(description = "Hedef içerik ID") @RequestParam UUID targetId,
            @Parameter(description = "Hedef içerik türü") @RequestParam LikeTargetType targetType) {
        UUID currentUserId = currentUserResolver.getCurrentUserId();
        LikeStatusResponse response = likeService.getLikeStatus(targetId, targetType, currentUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Kendi beğendiklerimi listele", description = "Mevcut kullanıcının beğendiği içerikleri sayfalar halinde döner.")
    public ResponseEntity<Page<LikedTargetResponse>> getMyLikes(
            @Parameter(description = "İçerik türü filtresi (opsiyonel)") @RequestParam(required = false) LikeTargetType targetType,
            @Parameter(description = "Sayfa numarası") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa boyutu") @RequestParam(defaultValue = "20") int size) {
        size = Math.min(size, 100); // Maksimum 100
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        UUID currentUserId = currentUserResolver.getCurrentUserId();
        Page<LikedTargetResponse> response = likeService.getMyLikes(currentUserId, targetType, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Kullanıcının beğendiklerini listele", description = "Belirtilen kullanıcının beğendiği içerikleri sayfalar halinde döner.")
    public ResponseEntity<Page<LikedTargetResponse>> getUserLikes(
            @Parameter(description = "Kullanıcı ID") @PathVariable UUID userId,
            @Parameter(description = "İçerik türü filtresi (opsiyonel)") @RequestParam(required = false) LikeTargetType targetType,
            @Parameter(description = "Sayfa numarası") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Sayfa boyutu") @RequestParam(defaultValue = "20") int size) {
        size = Math.min(size, 100); // Maksimum 100
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<LikedTargetResponse> response = likeService.getUserLikes(userId, targetType, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/count/bulk")
    @Operation(summary = "Toplu beğeni sayısı getir", description = "Birden fazla içeriğin beğeni sayısını tek istekle döner. Feed ekranları için idealdir.")
    public ResponseEntity<BulkLikeCountResponse> getBulkLikeCounts(@Valid @RequestBody BulkLikeCountRequest request) {
        BulkLikeCountResponse response = likeService.getBulkLikeCounts(request);
        return ResponseEntity.ok(response);
    }
}
