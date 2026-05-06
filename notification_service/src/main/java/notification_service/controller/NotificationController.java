package notification_service.controller;

import notification_service.dto.request.BulkNotificationRequest;
import notification_service.dto.request.CreateNotificationRequest;
import notification_service.dto.request.UpdateNotificationPreferenceRequest;
import notification_service.dto.response.ApiResponse;
import notification_service.dto.response.NotificationPreferenceResponse;
import notification_service.dto.response.NotificationResponse;
import notification_service.dto.response.PageResponse;
import notification_service.dto.response.UnreadCountResponse;
import notification_service.enums.NotificationPriority;
import notification_service.enums.NotificationType;
import notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ApiResponse<NotificationResponse> createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        NotificationResponse response = notificationService.createNotification(request);
        if (response == null) {
            return ApiResponse.success("Notification skipped", null);
        }
        return ApiResponse.success("Notification created", response);
    }

    @PostMapping("/bulk")
    public ApiResponse<Void> createBulk(@Valid @RequestBody BulkNotificationRequest request) {
        notificationService.createBulkNotification(request);
        return ApiResponse.success("Bulk notifications created", null);
    }

    @GetMapping("/me")
    public ApiResponse<PageResponse<NotificationResponse>> getMyNotifications(
        @RequestHeader("X-User-Id") Long userId,
        @RequestParam(required = false) Boolean read,
        @RequestParam(required = false) NotificationType type,
        @RequestParam(required = false) NotificationPriority priority,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

        Page<NotificationResponse> result = notificationService.getMyNotifications(
            userId, read, type, priority, PageRequest.of(page, size));
        return ApiResponse.success("Notifications fetched", toPageResponse(result));
    }

    @GetMapping("/me/unread")
    public ApiResponse<PageResponse<NotificationResponse>> getUnread(
        @RequestHeader("X-User-Id") Long userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

        Page<NotificationResponse> result = notificationService.getUnreadNotifications(userId, PageRequest.of(page, size));
        return ApiResponse.success("Unread notifications fetched", toPageResponse(result));
    }

    @GetMapping("/me/unread-count")
    public ApiResponse<UnreadCountResponse> getUnreadCount(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success("Unread count fetched", notificationService.getUnreadCount(userId));
    }

    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> markAsRead(@RequestHeader("X-User-Id") Long userId,
                                        @PathVariable Long notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return ApiResponse.success("Notification marked as read", null);
    }

    @PatchMapping("/me/read-all")
    public ApiResponse<Void> markAllAsRead(@RequestHeader("X-User-Id") Long userId) {
        notificationService.markAllAsRead(userId);
        return ApiResponse.success("All notifications marked as read", null);
    }

    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotification(@RequestHeader("X-User-Id") Long userId,
                                                @PathVariable Long notificationId) {
        notificationService.deleteNotification(userId, notificationId);
        return ApiResponse.success("Notification deleted", null);
    }

    @DeleteMapping("/me/clear")
    public ApiResponse<Void> clearMyNotifications(@RequestHeader("X-User-Id") Long userId) {
        notificationService.clearMyNotifications(userId);
        return ApiResponse.success("Notifications cleared", null);
    }

    @GetMapping("/me/preferences")
    public ApiResponse<NotificationPreferenceResponse> getPreferences(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success("Preferences fetched", notificationService.getPreferences(userId));
    }

    @PutMapping("/me/preferences")
    public ApiResponse<NotificationPreferenceResponse> updatePreferences(
        @RequestHeader("X-User-Id") Long userId,
        @Valid @RequestBody UpdateNotificationPreferenceRequest request) {
        return ApiResponse.success("Preferences updated", notificationService.updatePreferences(userId, request));
    }

    private PageResponse<NotificationResponse> toPageResponse(Page<NotificationResponse> page) {
        return PageResponse.<NotificationResponse>builder()
            .content(page.getContent())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }
}
