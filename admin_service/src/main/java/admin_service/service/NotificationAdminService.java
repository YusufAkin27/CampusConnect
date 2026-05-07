package admin_service.service;

import admin_service.dto.request.SendNotificationRequest;

import java.util.Map;

public interface NotificationAdminService {

    Map<String, Object> sendToUser(SendNotificationRequest request);

    Map<String, Object> sendToAll(SendNotificationRequest request);

    Map<String, Object> sendToDepartment(SendNotificationRequest request);

    Map<String, Object> sendToFaculty(SendNotificationRequest request);
}
