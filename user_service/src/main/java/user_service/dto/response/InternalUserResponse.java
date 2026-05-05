package user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.enums.AccountStatus;

/**
 * Internal user response for service-to-service communication.
 * Used by post-service, friend-service, chat-service, notification-service.
 * Contains essential identification and display information only.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalUserResponse {

    private Long id;
    private Long authUserId;
    private String username;
    private String email;
    private String displayName;
    private String profileImageUrl;
    private AccountStatus accountStatus;
}
