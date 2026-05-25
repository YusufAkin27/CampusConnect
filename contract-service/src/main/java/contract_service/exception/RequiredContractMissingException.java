package contract_service.exception;

public class RequiredContractMissingException extends RuntimeException {
    public RequiredContractMissingException(String message) {
        super(message);
    }
}
