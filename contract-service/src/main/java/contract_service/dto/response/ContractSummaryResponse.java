package contract_service.dto.response;

import contract_service.enums.ContractType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Sözleşme özet yanıtı - Kayıt ekranında kullanılır")
public class ContractSummaryResponse {

    @Schema(description = "Sözleşme ID")
    private UUID id;

    @Schema(description = "Sözleşme türü")
    private ContractType contractType;

    @Schema(description = "Sözleşme başlığı")
    private String title;

    @Schema(description = "Sözleşme versiyonu")
    private String version;

    @Schema(description = "Zorunlu mu?")
    private boolean isRequired;

    @Schema(description = "Yürürlük tarihi")
    private LocalDateTime effectiveDate;
}
