package contract_service.exception;

public class ContractAlreadyExistsException extends RuntimeException {
    public ContractAlreadyExistsException(String message) {
        super(message);
    }
}
