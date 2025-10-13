package in.connectwithsandeepan.interviewgenius.gatewayservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // Default 24 hours
    private long jwtExpiration;

    @Value("${jwt.issuer:interviewgenius-gateway}")
    private String jwtIssuer;

    @Value("${jwt.audience:interviewgenius-app}")
    private String jwtAudience;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Long userId, String email, String role, Set<String> authProviders,
                                String firstName, String lastName, String profilePicture) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        // Industry standard claims
        Map<String, Object> claims = new HashMap<>();

        // Custom claims (application specific)
        claims.put("userId", userId);  // Changed from user_id to userId for consistency
        claims.put("email", email);
        claims.put("role", role);
        claims.put("auth_providers", authProviders);
        claims.put("first_name", firstName);
        claims.put("last_name", lastName);
        claims.put("profile_picture", profilePicture != null ? profilePicture : "");

        return Jwts.builder()
            // Standard JWT claims
            .issuer(jwtIssuer)                          // iss: who issued the token
            .audience().add(jwtAudience).and()          // aud: who can use this token
            .subject(email)                              // sub: subject (user identifier)
            .id(UUID.randomUUID().toString())           // jti: unique token ID
            .issuedAt(now)                              // iat: when token was issued
            .expiration(expiryDate)                     // exp: when token expires
            .notBefore(now)                             // nbf: token valid from this time
            // Custom claims
            .claims(claims)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(getSigningKey())
                .requireIssuer(jwtIssuer)           // Validate issuer
                .requireAudience(jwtAudience)       // Validate audience
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return null;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        if (claims != null) {
            return claims.get("userId", Long.class);  // Changed from user_id to userId
        }
        return null;
    }

    public String getEmailFromToken(String token) {
        Claims claims = validateToken(token);
        if (claims != null) {
            // Can get from custom claim or standard sub claim
            String email = claims.get("email", String.class);
            if (email == null) {
                email = claims.getSubject();
            }
            return email;
        }
        return null;
    }

    public String getRoleFromToken(String token) {
        Claims claims = validateToken(token);
        if (claims != null) {
            return claims.get("role", String.class);
        }
        return null;
    }

    public String getFirstNameFromToken(String token) {
        Claims claims = validateToken(token);
        if (claims != null) {
            return claims.get("first_name", String.class);
        }
        return null;
    }

    public String getLastNameFromToken(String token) {
        Claims claims = validateToken(token);
        if (claims != null) {
            return claims.get("last_name", String.class);
        }
        return null;
    }

    public String getProfilePictureFromToken(String token) {
        Claims claims = validateToken(token);
        if (claims != null) {
            String picture = claims.get("profile_picture", String.class);
            return picture != null ? picture : "";
        }
        return "";
    }
}
