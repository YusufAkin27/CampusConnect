package contract_service.service;

import contract_service.dto.request.ContractValidationRequest;
import contract_service.dto.request.UserContractAcceptRequest;
import contract_service.dto.response.ContractSummaryResponse;
import contract_service.dto.response.ContractValidationResponse;
import contract_service.dto.response.UserContractAcceptanceResponse;
import contract_service.entity.Contract;
import contract_service.entity.UserContractAcceptance;
import contract_service.exception.ContractAlreadyAcceptedException;
import contract_service.exception.ContractNotActiveException;
import contract_service.exception.ContractNotFoundException;
import contract_service.repository.ContractRepository;
import contract_service.repository.UserContractAcceptanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserContractAcceptanceService {

    private final UserContractAcceptanceRepository acceptanceRepository;
    private final ContractRepository contractRepository;
    private final ContractService contractService;

    /**
     * Kullanıcının sözleşmeleri kabul etmesini kaydeder.
     * - Kullanıcı sadece aktif sözleşmeleri kabul edebilir.
     * - Aynı sözleşme ikinci kez kabul edilemez.
     * - ipAddress ve userAgent bilgileri kaydedilir.
     */
    @Transactional
    public List<UserContractAcceptanceResponse> acceptContracts(UserContractAcceptRequest request, String ipAddress, String userAgent) {
        UUID userId = request.getUserId();
        List<UserContractAcceptanceResponse> responses = new ArrayList<>();

        for (UUID contractId : request.getAcceptedContractIds()) {
            // Sözleşme var mı kontrol et
            Contract contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new ContractNotFoundException("Sözleşme bulunamadı: " + contractId));

            // Sözleşme aktif mi kontrol et
            if (!contract.isActive()) {
                throw new ContractNotActiveException(
                        String.format("Sözleşme aktif değil, kabul edilemez: %s (ID: %s)", contract.getTitle(), contractId));
            }

            // Daha önce kabul edilmiş mi kontrol et
            if (acceptanceRepository.existsByUserIdAndContractId(userId, contractId)) {
                throw new ContractAlreadyAcceptedException(
                        String.format("Bu sözleşme zaten kabul edilmiş: %s (ID: %s)", contract.getTitle(), contractId));
            }

            UserContractAcceptance acceptance = UserContractAcceptance.builder()
                    .userId(userId)
                    .contractId(contractId)
                    .contractType(contract.getContractType())
                    .contractVersion(contract.getVersion())
                    .acceptedAt(LocalDateTime.now())
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            UserContractAcceptance saved = acceptanceRepository.save(acceptance);
            responses.add(toAcceptanceResponse(saved));

            log.info("Kullanıcı {} sözleşmeyi kabul etti: {} v{} (ID: {})",
                    userId, contract.getContractType(), contract.getVersion(), contractId);
        }

        return responses;
    }

    /**
     * Kullanıcının kabul ettiği tüm sözleşmeleri listeler.
     */
    @Transactional(readOnly = true)
    public List<UserContractAcceptanceResponse> getUserAcceptances(UUID userId) {
        return acceptanceRepository.findByUserId(userId).stream()
                .map(this::toAcceptanceResponse)
                .collect(Collectors.toList());
    }

    /**
     * Kullanıcının belirli bir sözleşmeyi kabul edip etmediğini kontrol eder.
     */
    @Transactional(readOnly = true)
    public UserContractAcceptanceResponse getUserAcceptanceForContract(UUID userId, UUID contractId) {
        UserContractAcceptance acceptance = acceptanceRepository.findByUserIdAndContractId(userId, contractId)
                .orElseThrow(() -> new ContractNotFoundException(
                        String.format("Kullanıcı %s için sözleşme %s kabul kaydı bulunamadı", userId, contractId)));
        return toAcceptanceResponse(acceptance);
    }

    /**
     * Kayıt sırasında zorunlu sözleşmelerin kabul edilip edilmediğini doğrular.
     * Auth Service bu endpoint'i kullanarak kullanıcının tüm zorunlu sözleşmeleri
     * kabul ettiğinden emin olur.
     */
    @Transactional(readOnly = true)
    public ContractValidationResponse validateRequiredContracts(ContractValidationRequest request) {
        // Aktif ve zorunlu tüm sözleşmeleri al
        List<Contract> requiredContracts = contractRepository.findByIsActiveTrueAndIsRequiredTrue();

        if (requiredContracts.isEmpty()) {
            return ContractValidationResponse.builder()
                    .valid(true)
                    .missingRequiredContracts(Collections.emptyList())
                    .message("Zorunlu sözleşme bulunmamaktadır.")
                    .build();
        }

        Set<UUID> acceptedIds = new HashSet<>(request.getAcceptedContractIds());

        // Eksik zorunlu sözleşmeleri bul
        List<ContractSummaryResponse> missingContracts = requiredContracts.stream()
                .filter(contract -> !acceptedIds.contains(contract.getId()))
                .map(contractService::toContractSummaryResponse)
                .collect(Collectors.toList());

        boolean isValid = missingContracts.isEmpty();

        String message = isValid
                ? "Tüm zorunlu sözleşmeler kabul edilmiştir."
                : String.format("%d adet zorunlu sözleşme kabul edilmemiştir.", missingContracts.size());

        return ContractValidationResponse.builder()
                .valid(isValid)
                .missingRequiredContracts(missingContracts)
                .message(message)
                .build();
    }

    // ===== Mapper metodu =====

    private UserContractAcceptanceResponse toAcceptanceResponse(UserContractAcceptance acceptance) {
        return UserContractAcceptanceResponse.builder()
                .id(acceptance.getId())
                .userId(acceptance.getUserId())
                .contractId(acceptance.getContractId())
                .contractType(acceptance.getContractType())
                .contractVersion(acceptance.getContractVersion())
                .acceptedAt(acceptance.getAcceptedAt())
                .ipAddress(acceptance.getIpAddress())
                .userAgent(acceptance.getUserAgent())
                .build();
    }
}
