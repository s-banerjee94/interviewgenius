package in.connectwithsandeepan.interviewgenius.userservice.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT Authentication Filter that extracts user information from JWT tokens
 * forwarded by the Gateway Service and populates the Spring Security context.
 *
 * This filter does NOT validate JWT signatures - it trusts that the Gateway
 * has already performed validation. It only extracts user claims for authorization.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtParser jwtParser;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Extract Authorization header
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader != null) {
                // Extract token from "Bearer <token>"
                String token = jwtParser.extractTokenFromHeader(authorizationHeader);

                if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Parse token and extract user information (no validation)
                    UserPrincipal userPrincipal = jwtParser.parseTokenWithoutValidation(token);

                    // Create authorities with ROLE_ prefix (Spring Security convention)
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + userPrincipal.getRole().name())
                    );

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userPrincipal,
                                    null,
                                    authorities
                            );

                    // Set additional details
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("Authenticated user: {} with role: {}",
                            userPrincipal.getEmail(),
                            userPrincipal.getRole());
                }
            }

        } catch (JwtException e) {
            log.error("JWT parsing error: {}", e.getMessage());
            // Don't set authentication - request will be treated as unauthenticated
        } catch (Exception e) {
            log.error("Unexpected error in JWT filter: {}", e.getMessage(), e);
            // Continue filter chain even if there's an error
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // Don't filter certain endpoints that don't need JWT parsing
        String path = request.getRequestURI();

        // Skip JWT parsing for:
        // - Health checks (no auth needed)
        // - Internal service-to-service calls (called by Gateway without user JWT)
        return path.equals("/api/v1/actuator/health") ||
               path.startsWith("/api/v1/internal/");
    }
}
