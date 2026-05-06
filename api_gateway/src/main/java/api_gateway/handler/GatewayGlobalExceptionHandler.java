package api_gateway.handler;

import api_gateway.dto.ApiResponse;
import java.time.Instant;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GatewayGlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String code = "INTERNAL_GATEWAY_ERROR";

        if (ex instanceof org.springframework.cloud.gateway.support.NotFoundException) {
            status = HttpStatus.NOT_FOUND;
            code = "ROUTE_NOT_FOUND";
        }

        ApiResponse<Object> response = ApiResponse.builder()
            .success(false)
            .message(status.getReasonPhrase())
            .code(code)
            .path(exchange.getRequest().getURI().getPath())
            .requestId(exchange.getRequest().getHeaders().getFirst("X-Request-Id"))
            .timestamp(Instant.now())
            .build();

        byte[] bytes = ("{\"success\":false,\"message\":\"" + response.getMessage() +
            "\",\"code\":\"" + response.getCode() + "\",\"path\":\"" + response.getPath() +
            "\",\"requestId\":\"" + response.getRequestId() + "\",\"timestamp\":\"" +
            response.getTimestamp().toString() + "\"}").getBytes();

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
}
