package com.codigo.apigateway;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;


@Configuration
public class GatewayConfig {

    private final SecurityFilterGateway securityFilterGateway;

    public GatewayConfig(SecurityFilterGateway securityFilterGateway) {
        this.securityFilterGateway = securityFilterGateway;
    }

    @Bean
    @Primary
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("security",
                        (r) ->  r.path("/security/auth/**")
                                .uri("lb://security"))
                .route("alquiler",
                        (r) -> r.path("/alquiler/**")
                                .filters(f -> f.filter(securityFilterGateway))
                                .uri("lb://alquiler"))
                .route("calificacion",
                        (r) -> r.path("/calificacion/**")
                                .filters(f -> f.filter(securityFilterGateway))
                                .uri("lb://calificacion"))
                .route("vehiculos",
                        (r) -> r.path("/vehiculos/**")
                                .filters(f -> f.filter(securityFilterGateway))
                                .uri("lb://vehiculos"))
                    .build();
    }
}
