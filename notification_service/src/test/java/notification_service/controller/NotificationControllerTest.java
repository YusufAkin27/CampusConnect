package notification_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import notification_service.dto.request.UpdateNotificationPreferenceRequest;
import notification_service.dto.response.NotificationPreferenceResponse;
import notification_service.dto.response.NotificationResponse;
import notification_service.dto.response.UnreadCountResponse;
import notification_service.enums.NotificationChannel;
import notification_service.enums.NotificationPriority;
import notification_service.enums.NotificationType;
import notification_service.enums.TargetType;
import notification_service.service.NotificationService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getMyNotifications() throws Exception {
        NotificationResponse response = NotificationResponse.builder()
            .id(1L)
            .receiverUserId(1L)
            .type(NotificationType.CHAT_MESSAGE_RECEIVED)
            .channel(NotificationChannel.IN_APP)
            .priority(NotificationPriority.NORMAL)
            .title("Title")
            .message("Message")
            .targetType(TargetType.CHAT)
            .targetId("c1")
            .createdAt(LocalDateTime.now())
            .build();

        Mockito.when(notificationService.getMyNotifications(Mockito.eq(1L), Mockito.any(), Mockito.any(), Mockito.any(),
            Mockito.any())).thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/v1/api/notifications/me")
                .header("X-User-Id", "1"))
            .andExpect(status().isOk());
    }

    @Test
    void getUnreadCount() throws Exception {
        Mockito.when(notificationService.getUnreadCount(1L))
            .thenReturn(UnreadCountResponse.builder().unreadCount(2).build());

        mockMvc.perform(get("/v1/api/notifications/me/unread-count")
                .header("X-User-Id", "1"))
            .andExpect(status().isOk());
    }

    @Test
    void markAsRead() throws Exception {
        mockMvc.perform(patch("/v1/api/notifications/1/read")
                .header("X-User-Id", "1"))
            .andExpect(status().isOk());
    }

    @Test
    void updatePreferences() throws Exception {
        NotificationPreferenceResponse response = NotificationPreferenceResponse.builder()
            .userId(1L)
            .inAppEnabled(true)
            .build();
        Mockito.when(notificationService.updatePreferences(Mockito.eq(1L), Mockito.any()))
            .thenReturn(response);

        UpdateNotificationPreferenceRequest request = UpdateNotificationPreferenceRequest.builder()
            .inAppEnabled(true)
            .build();

        mockMvc.perform(put("/v1/api/notifications/me/preferences")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }
}
