package in.connectwithsandeepan.interviewgenius.gatewayservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@Slf4j
public class CorsConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsWebFilter corsWebFilter() {
        log.info("Configuring CORS filter with highest precedence");

        CorsConfiguration corsConfig = new CorsConfiguration();

        // Allow localhost development ports (3000, 4200, etc.)
        corsConfig.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "http://127.0.0.1:*"));

        // Allow all methods
        corsConfig.addAllowedMethod("*");

        // Allow all headers
        corsConfig.addAllowedHeader("*");

        // Allow credentials (JWT tokens)
        corsConfig.setAllowCredentials(true);

        // Cache for 1 hour
        corsConfig.setMaxAge(3600L);

        log.info("CORS allowed origins: localhost:*, 127.0.0.1:*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
