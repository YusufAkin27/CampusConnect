package user_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private Long authUserId;
    private String username;

    private String firstName;
    private String lastName;

    private String bio;
    private String profileImageUrl;

    private String phoneNumber;


    private ProfileVisibility profileVisibility;
    private AccountStatus accountStatus;
    private Boolean profileCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
