package contract_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Sözleşme doğrulama isteği - kayıt sırasında zorunlu sözleşmelerin kabul edilip edilmediğini kontrol eder")
public class ContractValidationRequest {

    @NotEmpty(message = "Kabul edilen sözleşme listesi boş olamaz")
    @Schema(description = "Kabul edilen sözleşme ID'leri")
    private List<UUID> acceptedContractIds;
}
