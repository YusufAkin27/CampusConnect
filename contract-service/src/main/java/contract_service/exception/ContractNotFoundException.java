package contract_service.exception;

public class ContractNotFoundException extends RuntimeException {
    public ContractNotFoundException(String message) {
        super(message);
    }
}
