package lk.udcreations.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
        		// User Service Routes
        		.route("user-service", r -> r.path("/api/v1/users/**", "/api/v1/role/**")
                        .uri("http://localhost:8090"))
        		// Product Service Routes
                .route("product-service", r -> r.path("/api/v1/category/**", "/api/v1/brand/**", 
                                                      "/api/v1/distributor/**", "/api/v1/product/**", "/api/v1/inventory/**")
                        .uri("http://localhost:8091"))
              .route("customer-service", r -> r.path("/api/v1/customers/**", "/api/v1/customergroup/**").uri("http://localhost:8092"))
              .route("sales-service", r -> r.path("/api/v1/sale/**", "/api/v1/salesitem/**", "/api/v1/returns/**").uri("http://localhost:8093"))
            //.route("inventory-service", r -> r.path("/inventory/**").uri("http://localhost:8084"))
            //.route("promotion-service", r -> r.path("/promotions/**").uri("http://localhost:8085"))
            //.route("loyalty-service", r -> r.path("/loyalty/**").uri("http://localhost:8086"))
            .build();
    }
}
