package in.connectwithsandeepan.interviewgenius.gatewayservice.config;

import in.connectwithsandeepan.interviewgenius.gatewayservice.security.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/auth/**").permitAll()
                .pathMatchers("/oauth2/**").permitAll()
                .pathMatchers("/login/oauth2/**").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().permitAll() // For now, allow all - you can secure later
            )
            .oauth2Login(oauth2 -> oauth2
                .authenticationSuccessHandler(oAuth2LoginSuccessHandler)
            );

        return http.build();
    }
}
