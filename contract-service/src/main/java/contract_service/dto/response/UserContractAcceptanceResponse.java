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
@Schema(description = "Kullanıcı sözleşme kabul kaydı yanıtı")
public class UserContractAcceptanceResponse {

    @Schema(description = "Kabul kaydı ID")
    private UUID id;

    @Schema(description = "Kullanıcı ID")
    private UUID userId;

    @Schema(description = "Sözleşme ID")
    private UUID contractId;

    @Schema(description = "Sözleşme türü")
    private ContractType contractType;

    @Schema(description = "Kabul edilen sözleşme versiyonu")
    private String contractVersion;

    @Schema(description = "Kabul tarihi")
    private LocalDateTime acceptedAt;

    @Schema(description = "IP adresi")
    private String ipAddress;

    @Schema(description = "User agent bilgisi")
    private String userAgent;
}
