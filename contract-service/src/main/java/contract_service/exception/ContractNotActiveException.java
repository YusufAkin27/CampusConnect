package contract_service.exception;

public class ContractNotActiveException extends RuntimeException {
    public ContractNotActiveException(String message) {
        super(message);
    }
}
