package admin_service.service.impl;

import admin_service.client.NotificationServiceClient;
import admin_service.dto.request.SendNotificationRequest;
import admin_service.enums.ActionType;
import admin_service.enums.TargetType;
import admin_service.security.AdminAction;
import admin_service.service.NotificationAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationAdminServiceImpl implements NotificationAdminService {

    private final NotificationServiceClient notificationServiceClient;

    @Override
    @AdminAction(actionType = ActionType.NOTIFICATION_SENT, targetType = TargetType.USER)
    public Map<String, Object> sendToUser(SendNotificationRequest request) {
        log.info("Sending notification to user: {}", request.getTargetUserId());
        return notificationServiceClient.sendToUser(toMap(request));
    }

    @Override
    @AdminAction(actionType = ActionType.NOTIFICATION_SENT, targetType = TargetType.USER, description = "Broadcast notification sent to all users")
    public Map<String, Object> sendToAll(SendNotificationRequest request) {
        log.info("Sending broadcast notification: {}", request.getTitle());
        return notificationServiceClient.sendToAll(toMap(request));
    }

    @Override
    @AdminAction(actionType = ActionType.NOTIFICATION_SENT, targetType = TargetType.USER)
    public Map<String, Object> sendToDepartment(SendNotificationRequest request) {
        log.info("Sending notification to department: {}", request.getDepartment());
        return notificationServiceClient.sendToDepartment(toMap(request));
    }

    @Override
    @AdminAction(actionType = ActionType.NOTIFICATION_SENT, targetType = TargetType.USER)
    public Map<String, Object> sendToFaculty(SendNotificationRequest request) {
        log.info("Sending notification to faculty: {}", request.getFaculty());
        return notificationServiceClient.sendToFaculty(toMap(request));
    }

    private Map<String, Object> toMap(SendNotificationRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", request.getTitle());
        map.put("message", request.getMessage());
        if (request.getTargetUserId() != null) map.put("targetUserId", request.getTargetUserId());
        if (request.getDepartment() != null) map.put("department", request.getDepartment());
        if (request.getFaculty() != null) map.put("faculty", request.getFaculty());
        if (request.getNotificationType() != null) map.put("notificationType", request.getNotificationType());
        return map;
    }
}
