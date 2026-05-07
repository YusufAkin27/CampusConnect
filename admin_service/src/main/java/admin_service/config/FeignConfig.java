package admin_service.config;

import admin_service.exception.ExternalServiceException;
import admin_service.exception.ServiceUnavailableException;
import feign.FeignException;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign client configuration.
 * - Propagates JWT token from incoming request to downstream service calls.
 * - Provides custom error decoder for translating Feign errors.
 */
@Configuration
@Slf4j
public class FeignConfig {

    /**
     * Propagates the Authorization header from the incoming request
     * to outgoing Feign client calls.
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null) {
                    requestTemplate.header("Authorization", authHeader);
                }
                // Propagate admin ID header
                String adminIdHeader = request.getHeader("X-Admin-Id");
                if (adminIdHeader != null) {
                    requestTemplate.header("X-Admin-Id", adminIdHeader);
                }
            }
        };
    }

    /**
     * Custom error decoder for Feign clients.
     * Translates HTTP errors from downstream services into custom exceptions.
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            String serviceName = extractServiceName(methodKey);
            int status = response.status();

            log.error("Feign error calling {}: status={}", methodKey, status);

            if (status == 503 || status == 502 || status == 504) {
                return new ServiceUnavailableException(serviceName);
            }

            String message = String.format("HTTP %d error from %s", status, methodKey);
            return new ExternalServiceException(serviceName, message);
        };
    }

    private String extractServiceName(String methodKey) {
        // methodKey format: "ClassName#methodName(ParamType)"
        if (methodKey.contains("#")) {
            String className = methodKey.substring(0, methodKey.indexOf("#"));
            return className.replace("Client", "").replace("Service", "");
        }
        return "unknown-service";
    }
}
