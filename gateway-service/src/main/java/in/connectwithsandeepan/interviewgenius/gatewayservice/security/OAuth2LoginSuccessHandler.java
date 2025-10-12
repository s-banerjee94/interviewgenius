package in.connectwithsandeepan.interviewgenius.gatewayservice.security;

import in.connectwithsandeepan.interviewgenius.gatewayservice.client.UserServiceClient;
import in.connectwithsandeepan.interviewgenius.gatewayservice.dto.CreateOAuthUserRequest;
import in.connectwithsandeepan.interviewgenius.gatewayservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final UserServiceClient userServiceClient;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauthToken.getPrincipal();
        ServerWebExchange exchange = webFilterExchange.getExchange();

        String providerString = oauthToken.getAuthorizedClientRegistrationId().toUpperCase();
        String email = extractEmail(oauth2User, providerString);
        String providerId = extractProviderId(oauth2User, providerString);

        if (email == null) {
            log.error("Email not found in OAuth2 response for provider: {}", providerString);
            return redirectToError(exchange, "Email not provided by OAuth provider");
        }

        log.info("OAuth2 login attempt - Provider: {}, Email: {}", providerString, email);

        // Check if user exists
        return userServiceClient.getUserByEmail(email)
            .flatMap(existingUser -> handleExistingUser(exchange, existingUser, providerString, providerId))
            .onErrorResume(error -> handleNewUser(exchange, oauth2User, providerString, providerId, email));
    }

    private Mono<Void> handleExistingUser(ServerWebExchange exchange, UserDto user, String provider, String providerId) {
        log.info("User found with email: {}", user.getEmail());

        // Check if this OAuth provider is already linked
        if (user.getAuthProviders().contains(provider)) {
            log.info("Provider {} already linked. Issuing token.", provider);
            return issueTokenAndRedirect(exchange, user);
        }

        // Hybrid approach: Auto-link the OAuth provider
        log.info("Auto-linking provider {} to user ID: {}", provider, user.getId());
        return userServiceClient.linkOAuthProvider(user.getId(), provider)
            .flatMap(updatedUser -> {
                log.info("Successfully linked provider {} to user ID: {}", provider, user.getId());
                return issueTokenAndRedirect(exchange, updatedUser);
            })
            .onErrorResume(error -> {
                log.error("Error linking provider: {}", error.getMessage());
                return redirectToError(exchange, "Failed to link OAuth provider");
            });
    }

    private Mono<Void> handleNewUser(ServerWebExchange exchange, OAuth2User oauth2User,
                                     String provider, String providerId, String email) {
        log.info("Creating new user for email: {} with provider: {}", email, provider);

        String firstName = extractFirstName(oauth2User);
        String lastName = extractLastName(oauth2User);
        String profileImageUrl = extractProfileImageUrl(oauth2User, provider);

        CreateOAuthUserRequest createRequest = CreateOAuthUserRequest.builder()
            .email(email)
            .firstName(firstName)
            .lastName(lastName)
            .authProvider(provider)
            .profileImageUrl(profileImageUrl)
            .build();

        return userServiceClient.createOAuthUser(createRequest)
            .flatMap(newUser -> {
                log.info("Successfully created OAuth user with ID: {}", newUser.getId());
                return issueTokenAndRedirect(exchange, newUser);
            })
            .onErrorResume(error -> {
                log.error("Error creating OAuth user: {}", error.getMessage());
                return redirectToError(exchange, "Failed to create user account");
            });
    }

    private Mono<Void> issueTokenAndRedirect(ServerWebExchange exchange, UserDto user) {
        String token = jwtTokenProvider.generateToken(
            user.getId(),
            user.getEmail(),
            user.getRole(),
            user.getAuthProviders(),
            user.getFirstName(),
            user.getLastName(),
            user.getProfileImageUrl()
        );

        log.info("JWT token issued for user: {}", user.getEmail());

        // Redirect to frontend with token
        String redirectUrl = String.format("http://localhost:3000/auth/callback?token=%s", token);

        return Mono.fromRunnable(() -> {
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FOUND);
            exchange.getResponse().getHeaders().setLocation(URI.create(redirectUrl));
        }).then();
    }

    private Mono<Void> redirectToError(ServerWebExchange exchange, String errorMessage) {
        log.error("Authentication error: {}", errorMessage);
        String redirectUrl = UriComponentsBuilder
            .fromUriString("http://localhost:3000/auth/error")
            .queryParam("message", errorMessage)
            .encode()  // Properly encode the URL with query params
            .toUriString();

        return Mono.fromRunnable(() -> {
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FOUND);
            exchange.getResponse().getHeaders().setLocation(URI.create(redirectUrl));
        }).then();
    }

    private String extractFirstName(OAuth2User oauth2User) {
        String givenName = oauth2User.getAttribute("given_name");
        if (givenName != null) return givenName;

        String name = oauth2User.getAttribute("name");
        if (name != null) {
            String[] parts = name.split(" ");
            return parts.length > 0 ? parts[0] : "User";
        }
        return "User";
    }

    private String extractLastName(OAuth2User oauth2User) {
        String familyName = oauth2User.getAttribute("family_name");
        if (familyName != null) return familyName;

        String name = oauth2User.getAttribute("name");
        if (name != null) {
            String[] parts = name.split(" ");
            return parts.length > 1 ? parts[parts.length - 1] : "";
        }
        return "";
    }

    private String extractEmail(OAuth2User oauth2User, String provider) {
        String email = oauth2User.getAttribute("email");

        // GitHub specific: If email is null/private, try alternatives
        if ((email == null || email.isEmpty()) && "GITHUB".equalsIgnoreCase(provider)) {
            // Try notification_email first
            email = oauth2User.getAttribute("notification_email");

            if (email != null && !email.isEmpty()) {
                log.info("GitHub primary email is private. Using notification_email from GitHub: {}", email);
            } else {
                // Fallback: Use GitHub username to create unique email
                String login = oauth2User.getAttribute("login");
                Integer githubId = oauth2User.getAttribute("id");

                if (login != null && githubId != null) {
                    // Use GitHub's standard noreply email format
                    email = githubId + "+" + login + "@users.noreply.github.com";
                    log.warn("GitHub email and notification_email are both null. " +
                        "Creating fallback email using GitHub ID and username: {}", email);
                } else {
                    log.error("CRITICAL: Cannot extract email, notification_email, login, or id from GitHub. " +
                        "Available attributes: {}", oauth2User.getAttributes().keySet());
                    return null;
                }
            }
        }

        return email;
    }

    private String extractProviderId(OAuth2User oauth2User, String provider) {
        // Google uses "sub" as the user identifier
        if ("GOOGLE".equalsIgnoreCase(provider)) {
            return oauth2User.getAttribute("sub");
        }

        // GitHub uses "id" as the user identifier
        if ("GITHUB".equalsIgnoreCase(provider)) {
            Integer id = oauth2User.getAttribute("id");
            return id != null ? id.toString() : null;
        }

        // Default fallback
        return oauth2User.getAttribute("sub");
    }

    private String extractProfileImageUrl(OAuth2User oauth2User, String provider) {
        // Google uses "picture"
        if ("GOOGLE".equalsIgnoreCase(provider)) {
            return oauth2User.getAttribute("picture");
        }

        // GitHub uses "avatar_url"
        if ("GITHUB".equalsIgnoreCase(provider)) {
            return oauth2User.getAttribute("avatar_url");
        }

        // Default fallback
        return oauth2User.getAttribute("picture");
    }
}
