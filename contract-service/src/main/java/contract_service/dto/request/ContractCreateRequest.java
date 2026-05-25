package contract_service.dto.request;

import contract_service.enums.ContractType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yeni sözleşme oluşturma isteği")
public class ContractCreateRequest {

    @NotNull(message = "Sözleşme türü boş olamaz")
    @Schema(description = "Sözleşme türü", example = "TERMS_OF_SERVICE")
    private ContractType contractType;

    @NotBlank(message = "Başlık boş olamaz")
    @Size(max = 500, message = "Başlık en fazla 500 karakter olabilir")
    @Schema(description = "Sözleşme başlığı", example = "Kullanım Koşulları")
    private String title;

    @NotBlank(message = "İçerik boş olamaz")
    @Schema(description = "Sözleşme içeriği")
    private String content;

    @NotBlank(message = "Versiyon boş olamaz")
    @Size(max = 50, message = "Versiyon en fazla 50 karakter olabilir")
    @Schema(description = "Sözleşme versiyonu", example = "1.0")
    private String version;

    @Schema(description = "Zorunlu sözleşme mi?", example = "true")
    private boolean isRequired;

    @Schema(description = "Yürürlük tarihi (boş bırakılırsa mevcut zaman atanır)")
    private LocalDateTime effectiveDate;
}
