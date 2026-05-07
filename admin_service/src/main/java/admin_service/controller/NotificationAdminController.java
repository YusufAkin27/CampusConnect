package admin_service.controller;

import admin_service.common.response.DataResponseMessage;
import admin_service.dto.request.SendNotificationRequest;
import admin_service.service.NotificationAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/admin/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Administration", description = "Send notifications to users")
public class NotificationAdminController {

    private final NotificationAdminService notificationAdminService;

    @PostMapping("/send-to-user")
    @PreAuthorize("hasAuthority('NOTIFICATION_SEND')")
    @Operation(summary = "Send notification to a specific user")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> sendToUser(@Valid @RequestBody SendNotificationRequest request) {
        return ResponseEntity.ok(DataResponseMessage.success("Notification sent to user.", notificationAdminService.sendToUser(request)));
    }

    @PostMapping("/send-to-all")
    @PreAuthorize("hasAuthority('NOTIFICATION_SEND')")
    @Operation(summary = "Send notification to all users")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> sendToAll(@Valid @RequestBody SendNotificationRequest request) {
        return ResponseEntity.ok(DataResponseMessage.success("Broadcast notification sent.", notificationAdminService.sendToAll(request)));
    }

    @PostMapping("/send-to-department")
    @PreAuthorize("hasAuthority('NOTIFICATION_SEND')")
    @Operation(summary = "Send notification to a department")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> sendToDepartment(@Valid @RequestBody SendNotificationRequest request) {
        return ResponseEntity.ok(DataResponseMessage.success("Department notification sent.", notificationAdminService.sendToDepartment(request)));
    }

    @PostMapping("/send-to-faculty")
    @PreAuthorize("hasAuthority('NOTIFICATION_SEND')")
    @Operation(summary = "Send notification to a faculty")
    public ResponseEntity<DataResponseMessage<Map<String, Object>>> sendToFaculty(@Valid @RequestBody SendNotificationRequest request) {
        return ResponseEntity.ok(DataResponseMessage.success("Faculty notification sent.", notificationAdminService.sendToFaculty(request)));
    }
}
