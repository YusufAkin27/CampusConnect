package comment_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReplyRequest {

    @NotBlank(message = "Cevap içeriği boş olamaz")
    @Size(min = 1, max = 1000, message = "Cevap 1-1000 karakter arasında olmalıdır")
    private String content;
}
