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
public class ChatMessageNotificationRequest {

    @NotNull
    private Long receiverUserId;

    @NotNull
    private Long senderUserId;

    @NotBlank
    private String conversationId;

    @NotBlank
    private String messageId;

    private String messagePreview;

    private String senderName;
}
