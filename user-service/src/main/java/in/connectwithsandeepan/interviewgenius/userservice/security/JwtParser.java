package in.connectwithsandeepan.interviewgenius.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.connectwithsandeepan.interviewgenius.userservice.entity.User;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;

/**
 * JWT Parser for extracting claims from tokens.
 * NOTE: This parser does NOT validate JWT signatures - it trusts that the Gateway Service
 * has already validated the token. This service only extracts user information from the claims.
 */
@Component
@Slf4j
public class JwtParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Parse JWT token and extract claims WITHOUT signature validation.
     * This assumes the Gateway Service has already validated the token.
     *
     * @param token The JWT token string
     * @return UserPrincipal containing user information from the token
     * @throws JwtException if token cannot be parsed
     */
    public UserPrincipal parseTokenWithoutValidation(String token) {
        try {
            // JWT format: header.payload.signature
            // We only need the payload (claims), skip signature validation
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new JwtException("Invalid JWT token format");
            }

            // Decode the payload (second part) - it's Base64 encoded JSON
            String payload = parts[1];
            byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
            String decodedPayload = new String(decodedBytes);

            // Parse JSON to Map and extract user information directly
            @SuppressWarnings("unchecked")
            Map<String, Object> claimsMap = objectMapper.readValue(decodedPayload, Map.class);

            return extractUserPrincipal(claimsMap);

        } catch (Exception e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            throw new JwtException("Failed to parse JWT token", e);
        }
    }

    /**
     * Extract UserPrincipal from JWT claims map.
     *
     * @param claimsMap JWT claims as a map
     * @return UserPrincipal with user information
     */
    private UserPrincipal extractUserPrincipal(Map<String, Object> claimsMap) {
        try {
            // Extract userId - handle both Integer and Long types
            Object userIdObj = claimsMap.get("userId");
            Long userId = null;
            if (userIdObj instanceof Number) {
                userId = ((Number) userIdObj).longValue();
            }

            String email = (String) claimsMap.get("email");
            String roleStr = (String) claimsMap.get("role");

            if (userId == null || email == null || roleStr == null) {
                throw new JwtException("Missing required claims in JWT token");
            }

            User.Role role = User.Role.valueOf(roleStr);

            return new UserPrincipal(userId, email, role);

        } catch (IllegalArgumentException e) {
            log.error("Invalid role in JWT claims: {}", e.getMessage());
            throw new JwtException("Invalid role in JWT token", e);
        }
    }

    /**
     * Extract JWT token from Authorization header.
     *
     * @param authorizationHeader The Authorization header value
     * @return JWT token string without "Bearer " prefix, or null if header is invalid
     */
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
