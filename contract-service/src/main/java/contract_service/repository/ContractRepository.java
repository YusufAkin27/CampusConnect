package contract_service.repository;

import contract_service.entity.Contract;
import contract_service.enums.ContractType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {

    /**
     * Aktif sözleşmeleri listeler.
     */
    List<Contract> findByIsActiveTrue();

    /**
     * Aktif ve zorunlu sözleşmeleri listeler.
     */
    List<Contract> findByIsActiveTrueAndIsRequiredTrue();

    /**
     * ID ile aktif sözleşme bulur.
     */
    Optional<Contract> findByIdAndIsActiveTrue(UUID id);

    /**
     * ContractType'a göre en güncel aktif sözleşmeyi bulur (effectiveDate'e göre sıralı).
     */
    @Query("SELECT c FROM Contract c WHERE c.contractType = :contractType AND c.isActive = true ORDER BY c.effectiveDate DESC LIMIT 1")
    Optional<Contract> findLatestActiveByContractType(@Param("contractType") ContractType contractType);

    /**
     * Aynı contractType ve version kombinasyonu var mı kontrol eder.
     */
    boolean existsByContractTypeAndVersion(ContractType contractType, String version);
}
