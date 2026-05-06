package notification_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class BasicNotificationRequest {

    @NotNull
    private Long receiverUserId;

    private Long senderUserId;

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    @NotBlank
    private String targetId;

    private String actionUrl;
}
