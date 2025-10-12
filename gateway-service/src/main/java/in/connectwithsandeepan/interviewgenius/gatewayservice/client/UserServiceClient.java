package in.connectwithsandeepan.interviewgenius.gatewayservice.client;

import in.connectwithsandeepan.interviewgenius.gatewayservice.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;
    private static final String USER_SERVICE_URL = "http://user-service";

    public Mono<UserDto> getUserByEmail(String email) {
        return webClientBuilder.build()
            .get()
            .uri(USER_SERVICE_URL + "/api/v1/internal/auth/user/email/{email}", email)
            .retrieve()
            .bodyToMono(UserDto.class)
            .doOnError(error -> log.error("Error fetching user by email: {}", error.getMessage()));
    }

    public Mono<Boolean> validatePassword(String email, String password) {
        ValidatePasswordRequest request = new ValidatePasswordRequest(email, password);

        return webClientBuilder.build()
            .post()
            .uri(USER_SERVICE_URL + "/api/v1/internal/auth/validate-password")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ValidatePasswordResponse.class)
            .map(ValidatePasswordResponse::isValid)
            .doOnError(error -> log.error("Error validating password: {}", error.getMessage()));
    }

    public Mono<UserDto> authenticateUser(String email, String password) {
        AuthRequest request = new AuthRequest(email, password);

        return webClientBuilder.build()
            .post()
            .uri(USER_SERVICE_URL + "/api/v1/internal/auth/authenticate")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(UserDto.class)
            .doOnError(error -> log.error("Error authenticating user: {}", error.getMessage()));
    }

    public Mono<UserDto> createUser(CreateUserRequest userRequest) {
        return webClientBuilder.build()
            .post()
            .uri(USER_SERVICE_URL + "/api/v1/users")
            .bodyValue(userRequest)
            .retrieve()
            .bodyToMono(UserDto.class)
            .doOnError(error -> log.error("Error creating user: {}", error.getMessage()));
    }

    public Mono<UserDto> createOAuthUser(CreateOAuthUserRequest oauthUserRequest) {
        return webClientBuilder.build()
            .post()
            .uri(USER_SERVICE_URL + "/api/v1/internal/auth/oauth-user")
            .bodyValue(oauthUserRequest)
            .retrieve()
            .bodyToMono(UserDto.class)
            .doOnError(error -> log.error("Error creating OAuth user: {}", error.getMessage()));
    }

    public Mono<UserDto> linkOAuthProvider(Long userId, String authProvider) {
        LinkOAuthProviderRequest request = LinkOAuthProviderRequest.builder()
            .userId(userId)
            .authProvider(authProvider)
            .build();

        return webClientBuilder.build()
            .post()
            .uri(USER_SERVICE_URL + "/api/v1/internal/auth/link-provider")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(UserDto.class)
            .doOnError(error -> log.error("Error linking OAuth provider: {}", error.getMessage()));
    }
}
