package api_gateway.filter;

import api_gateway.client.LoggingClient;
import api_gateway.dto.GatewayLogRequest;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayLoggingFilter implements GlobalFilter, Ordered {

    private final LoggingClient loggingClient;

    @Value("${logging.service.url:}")
    private String loggingServiceUrl;

    public GatewayLoggingFilter(LoggingClient loggingClient) {
        this.loggingClient = loggingClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            String requestId = request.getHeaders().getFirst(CorrelationIdFilter.REQUEST_ID);
            String correlationId = request.getHeaders().getFirst(CorrelationIdFilter.CORRELATION_ID);

            GatewayLogRequest logRequest = GatewayLogRequest.builder()
                .requestId(requestId)
                .correlationId(correlationId)
                .method(request.getMethodValue())
                .path(request.getURI().getPath())
                .query(request.getURI().getQuery())
                .routeId(exchange.getAttributeOrDefault("org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayRoute", null) != null
                    ? exchange.getAttribute("org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayRoute").toString()
                    : null)
                .serviceName(exchange.getRequest().getURI().getHost())
                .clientIp(request.getHeaders().getFirst("X-Forwarded-For"))
                .userAgent(request.getHeaders().getFirst("User-Agent"))
                .userId(request.getHeaders().getFirst("X-User-Id"))
                .statusCode(response.getStatusCode() != null ? response.getStatusCode().value() : 0)
                .durationMs(System.currentTimeMillis() - start)
                .timestamp(Instant.now())
                .build();

            loggingClient.sendGatewayLog(loggingServiceUrl, logRequest);
        }));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
