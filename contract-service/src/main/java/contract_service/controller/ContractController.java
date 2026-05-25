package contract_service.controller;

import contract_service.dto.request.ContractValidationRequest;
import contract_service.dto.request.UserContractAcceptRequest;
import contract_service.dto.response.*;
import contract_service.service.ContractService;
import contract_service.service.UserContractAcceptanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Public ve Auth Service tarafından kullanılacak sözleşme endpointleri.
 */
@RestController
@RequestMapping("/v1/api/contracts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contract", description = "Sözleşme yönetimi - Public ve Auth Service endpointleri")
public class ContractController {

    private final ContractService contractService;
    private final UserContractAcceptanceService acceptanceService;

    @GetMapping("/active")
    @Operation(summary = "Aktif sözleşmeleri listeler",
            description = "Tüm aktif sözleşmeleri özet olarak döner. Kayıt ekranında kullanılabilir.")
    public ResponseEntity<ApiResponse<List<ContractSummaryResponse>>> getActiveContracts() {
        log.debug("Aktif sözleşmeler istendi");
        List<ContractSummaryResponse> contracts = contractService.getActiveContracts();
        return ResponseEntity.ok(ApiResponse.success(contracts, "Aktif sözleşmeler başarıyla getirildi."));
    }

    @GetMapping("/required")
    @Operation(summary = "Aktif ve zorunlu sözleşmeleri listeler",
            description = "Kayıt sırasında kullanıcının kabul etmesi gereken zorunlu sözleşmeleri döner. Auth Service bu endpoint'i kullanır.")
    public ResponseEntity<ApiResponse<List<ContractSummaryResponse>>> getRequiredContracts() {
        log.debug("Zorunlu sözleşmeler istendi");
        List<ContractSummaryResponse> contracts = contractService.getActiveRequiredContracts();
        return ResponseEntity.ok(ApiResponse.success(contracts, "Zorunlu sözleşmeler başarıyla getirildi."));
    }

    @PostMapping("/validate-required")
    @Operation(summary = "Zorunlu sözleşme doğrulaması",
            description = "Kayıt sırasında gönderilen acceptedContractIds listesinin aktif zorunlu sözleşmeleri karşılayıp karşılamadığını kontrol eder. Auth Service bu endpoint'i kullanır.")
    public ResponseEntity<ApiResponse<ContractValidationResponse>> validateRequiredContracts(
            @Valid @RequestBody ContractValidationRequest request) {
        log.debug("Zorunlu sözleşme doğrulaması istendi: {}", request.getAcceptedContractIds());
        ContractValidationResponse validation = acceptanceService.validateRequiredContracts(request);
        String message = validation.isValid()
                ? "Tüm zorunlu sözleşmeler kabul edilmiştir."
                : "Eksik zorunlu sözleşmeler bulunmaktadır.";
        return ResponseEntity.ok(ApiResponse.success(validation, message));
    }

    @PostMapping("/accept")
    @Operation(summary = "Sözleşmeleri kabul et",
            description = "Kullanıcının kabul ettiği sözleşmeleri kaydeder. Auth Service kullanıcı oluşturulduktan sonra bu endpoint'i çağırır. IP adresi ve User-Agent otomatik olarak kaydedilir.")
    public ResponseEntity<ApiResponse<List<UserContractAcceptanceResponse>>> acceptContracts(
            @Valid @RequestBody UserContractAcceptRequest request,
            HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        log.info("Kullanıcı {} sözleşme kabul isteği: {}", request.getUserId(), request.getAcceptedContractIds());
        List<UserContractAcceptanceResponse> acceptances = acceptanceService.acceptContracts(request, ipAddress, userAgent);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(acceptances, "Sözleşmeler başarıyla kabul edildi."));
    }

    @GetMapping("/users/{userId}/accepted")
    @Operation(summary = "Kullanıcının kabul ettiği sözleşmeleri listeler",
            description = "Belirtilen kullanıcının kabul ettiği tüm sözleşmeleri listeler.")
    public ResponseEntity<ApiResponse<List<UserContractAcceptanceResponse>>> getUserAcceptedContracts(
            @Parameter(description = "Kullanıcı ID") @PathVariable UUID userId) {
        log.debug("Kullanıcı {} için kabul edilen sözleşmeler istendi", userId);
        List<UserContractAcceptanceResponse> acceptances = acceptanceService.getUserAcceptances(userId);
        return ResponseEntity.ok(ApiResponse.success(acceptances, "Kullanıcının kabul ettiği sözleşmeler başarıyla getirildi."));
    }

    @GetMapping("/users/{userId}/accepted/{contractId}")
    @Operation(summary = "Kullanıcının belirli bir sözleşmeyi kabul edip etmediğini kontrol eder",
            description = "Kullanıcının belirtilen sözleşmeyi kabul edip etmediğini kontrol eder ve kabul kaydını döner.")
    public ResponseEntity<ApiResponse<UserContractAcceptanceResponse>> getUserAcceptanceForContract(
            @Parameter(description = "Kullanıcı ID") @PathVariable UUID userId,
            @Parameter(description = "Sözleşme ID") @PathVariable UUID contractId) {
        log.debug("Kullanıcı {} için sözleşme {} kabul kontrolü", userId, contractId);
        UserContractAcceptanceResponse acceptance = acceptanceService.getUserAcceptanceForContract(userId, contractId);
        return ResponseEntity.ok(ApiResponse.success(acceptance, "Sözleşme kabul kaydı başarıyla getirildi."));
    }

    /**
     * İstemci IP adresini çözümler (proxy/load balancer arkasındaysa X-Forwarded-For header'ını kontrol eder).
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
