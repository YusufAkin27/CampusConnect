package api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().set("X-Content-Type-Options", "nosniff");
        response.getHeaders().set("X-Frame-Options", "DENY");
        response.getHeaders().set("Referrer-Policy", "no-referrer");
        response.getHeaders().set("X-XSS-Protection", "0");
        response.getHeaders().set("Permissions-Policy", "camera=(), microphone=(), geolocation=()");
        response.getHeaders().set("Cache-Control", "no-store");
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -9;
    }
}
