package user_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import user_service.enums.AccountStatus;

/**
 * Request DTO for admin account status update operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountStatusRequest {

    @NotNull(message = "Account status cannot be null")
    private AccountStatus accountStatus;
}
