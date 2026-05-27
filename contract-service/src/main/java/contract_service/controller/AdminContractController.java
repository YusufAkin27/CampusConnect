package contract_service.controller;

import contract_service.dto.request.ContractCreateRequest;
import contract_service.dto.request.ContractUpdateRequest;
import contract_service.dto.response.ApiResponse;
import contract_service.dto.response.ContractResponse;
import contract_service.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Admin tarafından kullanılacak sözleşme yönetim endpointleri.
 */
@RestController
@RequestMapping("/v1/api/admin/contracts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Contract", description = "Sözleşme yönetimi - Admin endpointleri")
public class AdminContractController {

    private final ContractService contractService;

    @PostMapping
    @Operation(summary = "Yeni sözleşme oluştur",
            description = "Yeni bir sözleşme oluşturur. Aynı contractType ve version kombinasyonu daha önce oluşturulmuşsa hata döner.")
    public ResponseEntity<ApiResponse<ContractResponse>> createContract(
            @Valid @RequestBody ContractCreateRequest request) {
        log.info("Yeni sözleşme oluşturma isteği: {} v{}", request.getContractType(), request.getVersion());
        ContractResponse response = contractService.createContract(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Sözleşme başarıyla oluşturuldu."));
    }

    @PutMapping("/{contractId}")
    @Operation(summary = "Sözleşmeyi güncelle",
            description = "Mevcut bir sözleşmenin bilgilerini günceller.")
    public ResponseEntity<ApiResponse<ContractResponse>> updateContract(
            @Parameter(description = "Sözleşme ID") @PathVariable UUID contractId,
            @Valid @RequestBody ContractUpdateRequest request) {
        log.info("Sözleşme güncelleme isteği: {}", contractId);
        ContractResponse response = contractService.updateContract(contractId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Sözleşme başarıyla güncellendi."));
    }

    @PatchMapping("/{contractId}/deactivate")
    @Operation(summary = "Sözleşmeyi pasif yap",
            description = "Belirtilen sözleşmeyi pasif hale getirir. Pasif sözleşmeler kullanıcılar tarafından kabul edilemez.")
    public ResponseEntity<ApiResponse<ContractResponse>> deactivateContract(
            @Parameter(description = "Sözleşme ID") @PathVariable UUID contractId) {
        log.info("Sözleşme pasif yapma isteği: {}", contractId);
        ContractResponse response = contractService.deactivateContract(contractId);
        return ResponseEntity.ok(ApiResponse.success(response, "Sözleşme başarıyla pasif yapıldı."));
    }

    @GetMapping
    @Operation(summary = "Tüm sözleşmeleri listele",
            description = "Tüm sözleşmeleri (aktif ve pasif) listeler.")
    public ResponseEntity<ApiResponse<List<ContractResponse>>> getAllContracts() {
        log.debug("Tüm sözleşmeler istendi");
        List<ContractResponse> contracts = contractService.getAllContracts();
        return ResponseEntity.ok(ApiResponse.success(contracts, "Tüm sözleşmeler başarıyla getirildi."));
    }

    @GetMapping("/{contractId}")
    @Operation(summary = "Sözleşme detayını getir",
            description = "Belirtilen sözleşmenin tüm detay bilgilerini döner.")
    public ResponseEntity<ApiResponse<ContractResponse>> getContractById(
            @Parameter(description = "Sözleşme ID") @PathVariable UUID contractId) {
        log.debug("Sözleşme detayı istendi: {}", contractId);
        ContractResponse response = contractService.getContractById(contractId);
        return ResponseEntity.ok(ApiResponse.success(response, "Sözleşme detayı başarıyla getirildi."));
    }
}
