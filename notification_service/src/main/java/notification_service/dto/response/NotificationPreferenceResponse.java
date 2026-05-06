package notification_service.dto.response;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceResponse {

    private Long userId;
    private boolean inAppEnabled;
    private boolean pushEnabled;
    private boolean emailEnabled;
    private boolean chatNotificationsEnabled;
    private boolean eventNotificationsEnabled;
    private boolean friendNotificationsEnabled;
    private boolean postNotificationsEnabled;
    private boolean marketingNotificationsEnabled;
    private boolean quietHoursEnabled;
    private LocalTime quietHoursStart;
    private LocalTime quietHoursEnd;
}
