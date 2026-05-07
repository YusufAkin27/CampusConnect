package admin_service.exception;

/**
 * Thrown when a downstream microservice is unreachable or unhealthy.
 */
public class ServiceUnavailableException extends RuntimeException {

    private final String serviceName;

    public ServiceUnavailableException(String serviceName) {
        super(String.format("Service '%s' is currently unavailable. Please try again later.", serviceName));
        this.serviceName = serviceName;
    }

    public ServiceUnavailableException(String serviceName, Throwable cause) {
        super(String.format("Service '%s' is currently unavailable. Please try again later.", serviceName), cause);
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
