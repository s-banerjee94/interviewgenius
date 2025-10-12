package in.connectwithsandeepan.interviewgenius.gatewayservice.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().toString();

        // Skip JWT validation for OPTIONS (CORS preflight) requests
        if ("OPTIONS".equals(method)) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest());

        if (token != null) {
            Claims claims = jwtTokenProvider.validateToken(token);

            if (claims != null) {
                // Token is valid, extract user information
                String email = claims.get("email", String.class);
                String role = claims.get("role", String.class);
                Long userId = claims.get("user_id", Long.class);

                log.debug("JWT authenticated: {} (ID: {}) - {} {}", email, userId, method, path);

                // Create authentication object
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + (role != null ? role : "USER"))
                );

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

                // Add user information to request headers for downstream services
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId.toString())
                    .header("X-User-Email", email)
                    .header("X-User-Role", role != null ? role : "USER")
                    .build();

                ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(modifiedRequest)
                    .build();

                // Set authentication in security context and continue
                return chain.filter(modifiedExchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            } else {
                // Invalid token
                log.warn("Invalid JWT token for {} {}", method, path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        // No token provided, continue without authentication
        // The security config will handle authorization
        return chain.filter(exchange);
    }

    /**
     * Extracts JWT token from Authorization header
     * Expected format: "Bearer <token>"
     */
    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
