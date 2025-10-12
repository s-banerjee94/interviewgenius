package in.connectwithsandeepan.interviewgenius.gatewayservice.config;

import in.connectwithsandeepan.interviewgenius.gatewayservice.security.JwtAuthenticationEntryPoint;
import in.connectwithsandeepan.interviewgenius.gatewayservice.security.JwtAuthenticationFilter;
import in.connectwithsandeepan.interviewgenius.gatewayservice.security.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            // Disable session management - we're using JWT tokens
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            // Custom authentication entry point - returns 401 instead of redirecting
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers("/auth/login", "/auth/signup", "/auth/validate").permitAll()
                .pathMatchers("/oauth2/**").permitAll()
                .pathMatchers("/login/oauth2/**").permitAll()
                .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                .pathMatchers("/api/v1/**").authenticated()
                .pathMatchers("/actuator/**").authenticated()
                .anyExchange().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .authenticationSuccessHandler(oAuth2LoginSuccessHandler)
            )
            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}
