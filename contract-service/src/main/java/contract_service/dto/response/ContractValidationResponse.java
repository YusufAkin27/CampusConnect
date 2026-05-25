package contract_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Sözleşme doğrulama yanıtı - Zorunlu sözleşmelerin kabul edilip edilmediğini belirtir")
public class ContractValidationResponse {

    @Schema(description = "Doğrulama sonucu", example = "true")
    private boolean valid;

    @Schema(description = "Eksik zorunlu sözleşmeler")
    private List<ContractSummaryResponse> missingRequiredContracts;

    @Schema(description = "Doğrulama mesajı")
    private String message;
}
