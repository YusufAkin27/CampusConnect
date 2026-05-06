package api_gateway.filter;

import api_gateway.util.RequestIdUtils;
import java.util.UUID;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    public static final String REQUEST_ID = "X-Request-Id";
    public static final String CORRELATION_ID = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = RequestIdUtils.resolveRequestId(exchange.getRequest().getHeaders().getFirst(REQUEST_ID));
        String correlationId = RequestIdUtils.resolveCorrelationId(
            exchange.getRequest().getHeaders().getFirst(CORRELATION_ID), requestId);

        ServerHttpRequest request = exchange.getRequest().mutate()
            .header(REQUEST_ID, requestId)
            .header(CORRELATION_ID, correlationId)
            .build();

        exchange.getResponse().getHeaders().set(REQUEST_ID, requestId);
        exchange.getResponse().getHeaders().set(CORRELATION_ID, correlationId);

        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return -10;
    }
}
