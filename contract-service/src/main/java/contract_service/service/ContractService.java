package contract_service.service;

import contract_service.dto.request.ContractCreateRequest;
import contract_service.dto.request.ContractUpdateRequest;
import contract_service.dto.response.ContractResponse;
import contract_service.dto.response.ContractSummaryResponse;
import contract_service.entity.Contract;
import contract_service.exception.ContractAlreadyExistsException;
import contract_service.exception.ContractNotFoundException;
import contract_service.exception.InvalidContractException;
import contract_service.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractService {

    private final ContractRepository contractRepository;

    /**
     * Yeni sözleşme oluşturur.
     * Aynı contractType ve version kombinasyonu varsa hata fırlatır.
     */
    @Transactional
    public ContractResponse createContract(ContractCreateRequest request) {
        // Aynı contractType ve version kontrolü
        if (contractRepository.existsByContractTypeAndVersion(request.getContractType(), request.getVersion())) {
            throw new ContractAlreadyExistsException(
                    String.format("Bu sözleşme türü ve versiyonda bir sözleşme zaten mevcut: %s v%s",
                            request.getContractType(), request.getVersion()));
        }

        Contract contract = Contract.builder()
                .contractType(request.getContractType())
                .title(request.getTitle())
                .content(request.getContent())
                .version(request.getVersion())
                .isRequired(request.isRequired())
                .isActive(true)
                .effectiveDate(request.getEffectiveDate() != null ? request.getEffectiveDate() : LocalDateTime.now())
                .build();

        Contract saved = contractRepository.save(contract);
        log.info("Yeni sözleşme oluşturuldu: {} v{} (ID: {})", saved.getContractType(), saved.getVersion(), saved.getId());
        return toContractResponse(saved);
    }

    /**
     * Mevcut bir sözleşmeyi günceller.
     */
    @Transactional
    public ContractResponse updateContract(UUID contractId, ContractUpdateRequest request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ContractNotFoundException("Sözleşme bulunamadı: " + contractId));

        // Version değişiyorsa aynı type+version kontrolü
        if (request.getVersion() != null && !request.getVersion().equals(contract.getVersion())) {
            if (contractRepository.existsByContractTypeAndVersion(contract.getContractType(), request.getVersion())) {
                throw new ContractAlreadyExistsException(
                        String.format("Bu sözleşme türü ve versiyonda bir sözleşme zaten mevcut: %s v%s",
                                contract.getContractType(), request.getVersion()));
            }
            contract.setVersion(request.getVersion());
        }

        if (request.getTitle() != null) {
            contract.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            contract.setContent(request.getContent());
        }
        if (request.getIsRequired() != null) {
            contract.setRequired(request.getIsRequired());
        }
        if (request.getIsActive() != null) {
            contract.setActive(request.getIsActive());
        }
        if (request.getEffectiveDate() != null) {
            contract.setEffectiveDate(request.getEffectiveDate());
        }

        Contract updated = contractRepository.save(contract);
        log.info("Sözleşme güncellendi: {} (ID: {})", updated.getContractType(), updated.getId());
        return toContractResponse(updated);
    }

    /**
     * Sözleşmeyi pasif yapar.
     */
    @Transactional
    public ContractResponse deactivateContract(UUID contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ContractNotFoundException("Sözleşme bulunamadı: " + contractId));

        contract.setActive(false);
        Contract updated = contractRepository.save(contract);
        log.info("Sözleşme pasif yapıldı: {} (ID: {})", updated.getContractType(), updated.getId());
        return toContractResponse(updated);
    }

    /**
     * ID ile sözleşme getirir.
     */
    @Transactional(readOnly = true)
    public ContractResponse getContractById(UUID contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ContractNotFoundException("Sözleşme bulunamadı: " + contractId));
        return toContractResponse(contract);
    }

    /**
     * Tüm sözleşmeleri listeler.
     */
    @Transactional(readOnly = true)
    public List<ContractResponse> getAllContracts() {
        return contractRepository.findAll().stream()
                .map(this::toContractResponse)
                .collect(Collectors.toList());
    }

    /**
     * Aktif sözleşmeleri listeler (ContractSummaryResponse olarak).
     */
    @Transactional(readOnly = true)
    public List<ContractSummaryResponse> getActiveContracts() {
        return contractRepository.findByIsActiveTrue().stream()
                .map(this::toContractSummaryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Aktif ve zorunlu sözleşmeleri listeler (ContractSummaryResponse olarak).
     */
    @Transactional(readOnly = true)
    public List<ContractSummaryResponse> getActiveRequiredContracts() {
        return contractRepository.findByIsActiveTrueAndIsRequiredTrue().stream()
                .map(this::toContractSummaryResponse)
                .collect(Collectors.toList());
    }

    /**
     * ID ile aktif sözleşme bulur. Bulamazsa hata fırlatır.
     */
    @Transactional(readOnly = true)
    public Contract getActiveContractEntityById(UUID contractId) {
        return contractRepository.findByIdAndIsActiveTrue(contractId)
                .orElseThrow(() -> new ContractNotFoundException("Aktif sözleşme bulunamadı: " + contractId));
    }

    /**
     * ID ile sözleşme entity'si bulur. Bulamazsa hata fırlatır.
     */
    @Transactional(readOnly = true)
    public Contract getContractEntityById(UUID contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new ContractNotFoundException("Sözleşme bulunamadı: " + contractId));
    }

    // ===== Mapper metodları =====

    private ContractResponse toContractResponse(Contract contract) {
        return ContractResponse.builder()
                .id(contract.getId())
                .contractType(contract.getContractType())
                .title(contract.getTitle())
                .content(contract.getContent())
                .version(contract.getVersion())
                .isRequired(contract.isRequired())
                .isActive(contract.isActive())
                .effectiveDate(contract.getEffectiveDate())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }

    public ContractSummaryResponse toContractSummaryResponse(Contract contract) {
        return ContractSummaryResponse.builder()
                .id(contract.getId())
                .contractType(contract.getContractType())
                .title(contract.getTitle())
                .version(contract.getVersion())
                .isRequired(contract.isRequired())
                .effectiveDate(contract.getEffectiveDate())
                .build();
    }
}
