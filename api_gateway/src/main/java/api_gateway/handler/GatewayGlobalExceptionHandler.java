package api_gateway.handler;

import api_gateway.dto.ApiResponse;
import java.net.ConnectException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(-2)
public class GatewayGlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String code = "INTERNAL_GATEWAY_ERROR";
        String message = "Internal Server Error";

        if (ex instanceof org.springframework.cloud.gateway.support.NotFoundException) {
            status = HttpStatus.NOT_FOUND;
            code = "ROUTE_NOT_FOUND";
            message = "Service route not found";
        } else if (ex instanceof ConnectException || hasRootCause(ex, ConnectException.class)) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            code = "SERVICE_UNAVAILABLE";
            message = "Downstream service is unavailable";
        } else if (ex instanceof TimeoutException || hasRootCause(ex, TimeoutException.class)) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            code = "GATEWAY_TIMEOUT";
            message = "Downstream service timed out";
        } else if (ex instanceof org.springframework.web.server.ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            code = "RESPONSE_STATUS_ERROR";
            message = rse.getReason() != null ? rse.getReason() : status.getReasonPhrase();
        }

        log.error("Gateway error [{}] {} {}: {}",
            status.value(),
            exchange.getRequest().getMethod(),
            exchange.getRequest().getURI().getPath(),
            ex.getMessage(), ex);

        String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-Id");
        String path = exchange.getRequest().getURI().getPath();

        String json = String.format(
            "{\"success\":false,\"message\":\"%s\",\"code\":\"%s\",\"path\":\"%s\",\"requestId\":\"%s\",\"timestamp\":\"%s\"}",
            escapeJson(message),
            escapeJson(code),
            escapeJson(path != null ? path : ""),
            escapeJson(requestId != null ? requestId : ""),
            Instant.now().toString()
        );

        byte[] bytes = json.getBytes();
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }

    private boolean hasRootCause(Throwable ex, Class<? extends Throwable> type) {
        Throwable cause = ex;
        int depth = 0;
        while (cause != null && depth < 10) {
            if (type.isInstance(cause)) {
                return true;
            }
            cause = cause.getCause();
            depth++;
        }
        return false;
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
