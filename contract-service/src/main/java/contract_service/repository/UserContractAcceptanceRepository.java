package contract_service.repository;

import contract_service.entity.UserContractAcceptance;
import contract_service.enums.ContractType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserContractAcceptanceRepository extends JpaRepository<UserContractAcceptance, UUID> {

    /**
     * Kullanıcının belirli bir sözleşmeyi kabul edip etmediğini kontrol eder.
     */
    boolean existsByUserIdAndContractId(UUID userId, UUID contractId);

    /**
     * Kullanıcının kabul ettiği tüm sözleşmeleri listeler.
     */
    List<UserContractAcceptance> findByUserId(UUID userId);

    /**
     * Kullanıcının belirli bir sözleşme türüne ait kabul kayıtlarını listeler.
     */
    List<UserContractAcceptance> findByUserIdAndContractType(UUID userId, ContractType contractType);

    /**
     * Kullanıcı ve sözleşme ID'si ile kabul kaydını bulur.
     */
    Optional<UserContractAcceptance> findByUserIdAndContractId(UUID userId, UUID contractId);
}
