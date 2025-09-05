package lk.udcreations.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
        		// User Service Routes
        		.route("user-service", r -> r.path("/api/v1/users/**", "/api/v1/role/**")
                        //.uri("http://localhost:8090"))
        				.uri("https://user-service-production-8940.up.railway.app"))
        		// Product Service Routes
                .route("product-service", r -> r.path("/api/v1/category/**", "/api/v1/brand/**", 
                                                      "/api/v1/distributor/**", "/api/v1/product/**", "/api/v1/inventory/**")
                        //.uri("http://localhost:8091"))
                		.uri("https://product-service-production-2fdc.up.railway.app"))
                // Customer Service Routes
              .route("customer-service", r -> r.path("/api/v1/customers/**", "/api/v1/customergroup/**")
            		  //.uri("http://localhost:8092"))
            		  .uri("https://customer-service-production-fdad.up.railway.app"))
                // Sales Service Routes
              .route("sales-service", r -> r.path("/api/v1/sale/**", "/api/v1/salesitem/**", "/api/v1/returns/**")
            		  //.uri("http://localhost:8093"))
            		  .uri("https://sale-service-production.up.railway.app"))
                // File Service Routes
                .route("file-service", r -> r.path("/api/v1/image/**")
                		//.uri("http://localhost:8094"))
                		.uri("https://file-service-production-81aa.up.railway.app"))
                // chat-bot Service Routes
                .route("chatbot-service", r -> r.path("/api/v1/chat/**")
                		//.uri("http://localhost:8095"))
                		.uri("http://localhost:8095"))
            //.route("inventory-service", r -> r.path("/inventory/**").uri("http://localhost:8084"))
            //.route("promotion-service", r -> r.path("/promotions/**").uri("http://localhost:8085"))
            //.route("loyalty-service", r -> r.path("/loyalty/**").uri("http://localhost:8086"))
            .build();
    }
}
