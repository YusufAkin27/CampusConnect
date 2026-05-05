package friend_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient configuration for friend-service.
 *
 * Creates a load-balanced WebClient.Builder bean that resolves service names
 * via Consul (e.g., "http://user-service/...").
 *
 * The @LoadBalanced annotation instructs Spring Cloud to intercept requests
 * and resolve the service name through the Consul service registry.
 */
@Configuration
public class WebClientConfig {

    @Value("${user-service.base-url:http://user-service}")
    private String userServiceBaseUrl;

    /**
     * A load-balanced WebClient.Builder for use with Consul service discovery.
     * Inject WebClient.Builder (not WebClient) into client classes for reusability.
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    /**
     * Pre-configured WebClient pointed at user-service.
     * Can be used directly in UserServiceClient.
     */
    @Bean
    public WebClient userServiceWebClient(@LoadBalanced WebClient.Builder builder) {
        return builder.baseUrl(userServiceBaseUrl).build();
    }
}
