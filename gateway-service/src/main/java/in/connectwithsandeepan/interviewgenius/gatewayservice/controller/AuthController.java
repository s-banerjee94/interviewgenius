package in.connectwithsandeepan.interviewgenius.gatewayservice.controller;

import in.connectwithsandeepan.interviewgenius.gatewayservice.client.UserServiceClient;
import in.connectwithsandeepan.interviewgenius.gatewayservice.dto.*;
import in.connectwithsandeepan.interviewgenius.gatewayservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserServiceClient userServiceClient;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public Mono<ResponseEntity<Object>> signup(@RequestBody SignupRequest request) {
        log.info("Signup request for email: {}", request.getEmail());

        CreateUserRequest userRequest = CreateUserRequest.builder()
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .password(request.getPassword())
            .phoneNumber(request.getPhoneNumber())
            .build();

        return userServiceClient.createUser(userRequest)
            .map(user -> {
                log.info("User signed up successfully: {}", user.getEmail());
                return ResponseEntity.status(HttpStatus.CREATED).build();
            })
            .onErrorResume(error -> {
                log.error("Signup error: {}", error.getMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
            });
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
        log.info("Login request for email: {}", request.getEmail());

        return userServiceClient.authenticateUser(request.getEmail(), request.getPassword())
            .map(user -> {
                String token = jwtTokenProvider.generateToken(
                    user.getId(),
                    user.getEmail(),
                    user.getRole(),
                    user.getAuthProviders(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getProfileImageUrl()
                );

                AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .expiresIn(86400L) // 24 hours in seconds
                    .build();

                log.info("User logged in successfully: {}", user.getEmail());
                return ResponseEntity.ok(response);
            })
            .onErrorResume(error -> {
                log.error("Login error: {}", error.getMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
            });
    }
}
