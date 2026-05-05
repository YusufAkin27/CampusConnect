package post_service.client.dto;

import lombok.*;

/**
 * DTO received from user-service internal endpoint.
 * Maps to /v1/api/users/internal/auth/{authUserId}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalUserResponse {

    private Long id;
    private Long authUserId;
    private String username;
    private String displayName;
    private String profileImageUrl;
    private String faculty;
    private String department;
    private String grade;
    private String accountStatus;
}
