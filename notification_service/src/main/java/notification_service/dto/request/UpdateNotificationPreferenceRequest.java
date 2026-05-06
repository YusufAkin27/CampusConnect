package notification_service.dto.request;

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
public class UpdateNotificationPreferenceRequest {

    private Boolean inAppEnabled;
    private Boolean pushEnabled;
    private Boolean emailEnabled;
    private Boolean chatNotificationsEnabled;
    private Boolean eventNotificationsEnabled;
    private Boolean friendNotificationsEnabled;
    private Boolean postNotificationsEnabled;
    private Boolean marketingNotificationsEnabled;
    private Boolean quietHoursEnabled;
    private LocalTime quietHoursStart;
    private LocalTime quietHoursEnd;
}
