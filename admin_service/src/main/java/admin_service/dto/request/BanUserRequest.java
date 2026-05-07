package admin_service.dto.request;

import admin_service.enums.BanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BanUserRequest {

    @NotBlank(message = "Ban reason is required")
    private String reason;

    @NotNull(message = "Ban type is required")
    private BanType banType;

    /** Required for TEMPORARY bans. */
    private LocalDateTime expiresAt;
}
