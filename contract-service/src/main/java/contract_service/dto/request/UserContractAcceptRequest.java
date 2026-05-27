package contract_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Kullanıcının sözleşme kabul isteği")
public class UserContractAcceptRequest {

    @NotNull(message = "Kullanıcı ID boş olamaz")
    @Schema(description = "Kullanıcı ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @NotEmpty(message = "Kabul edilen sözleşme listesi boş olamaz")
    @Schema(description = "Kabul edilen sözleşme ID'leri")
    private List<UUID> acceptedContractIds;
}
