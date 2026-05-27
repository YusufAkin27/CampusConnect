package contract_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Sözleşme güncelleme isteği")
public class ContractUpdateRequest {

    @Size(max = 500, message = "Başlık en fazla 500 karakter olabilir")
    @Schema(description = "Sözleşme başlığı", example = "Güncellenmiş Kullanım Koşulları")
    private String title;

    @Schema(description = "Sözleşme içeriği")
    private String content;

    @Size(max = 50, message = "Versiyon en fazla 50 karakter olabilir")
    @Schema(description = "Sözleşme versiyonu", example = "1.1")
    private String version;

    @Schema(description = "Zorunlu sözleşme mi?", example = "true")
    private Boolean isRequired;

    @Schema(description = "Aktif mi?", example = "true")
    private Boolean isActive;

    @Schema(description = "Yürürlük tarihi")
    private LocalDateTime effectiveDate;
}
