package logging_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient configuration for inter-service HTTP communication.
 *
 * TODO: Replace HTTP log ingestion with Kafka/RabbitMQ event consumer in production scale.
 * WebClient is used here for any outbound calls, not for receiving logs.
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .build();
    }
}
