package contract_service.exception;

public class ContractAlreadyAcceptedException extends RuntimeException {
    public ContractAlreadyAcceptedException(String message) {
        super(message);
    }
}
