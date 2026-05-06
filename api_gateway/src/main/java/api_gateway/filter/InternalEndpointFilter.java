package api_gateway.filter;

import api_gateway.util.PathUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class InternalEndpointFilter implements GlobalFilter, Ordered {

    private static final List<String> INTERNAL_PATHS = List.of(
        "/v1/api/media/internal",
        "/v1/api/logs/internal",
        "/v1/api/notifications/internal",
        "/v1/api/events/internal",
        "/v1/api/chats/internal",
        "/v1/api/users/internal",
        "/v1/api/friends/internal"
    );

    @Value("${internal.service.token:}")
    private String internalToken;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (!PathUtils.matchesAny(path, INTERNAL_PATHS)) {
            return chain.filter(exchange);
        }
        String header = exchange.getRequest().getHeaders().getFirst("X-Internal-Token");
        if (internalToken == null || internalToken.isBlank() || header == null || !internalToken.equals(header)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -8;
    }
}
