package notification_service.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSummary {
    private Long id;
    private String username;
    private String fullName;
    private String profilePhotoUrl;
    private String department;
    private String faculty;
    private String grade;
    private boolean isActive;
}
