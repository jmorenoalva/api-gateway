package com.codigo.apigateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class SecurityFilterGateway implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> auth = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (auth == null) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        WebClient.ResponseSpec retrieve = WebClient.create().get()
                .uri("http://localhost:8099/security/auth/valid")
                .header(HttpHeaders.AUTHORIZATION, auth.get(0)).retrieve();
        return  retrieve.bodyToMono(Boolean.class)
                .onErrorReturn(Boolean.FALSE)
                .flatMap(val -> {
                    if (val) {
                        return chain.filter(exchange);
                    }
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                });
    }
}
