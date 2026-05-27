package contract_service.exception;

public class InvalidContractException extends RuntimeException {
    public InvalidContractException(String message) {
        super(message);
    }
}
