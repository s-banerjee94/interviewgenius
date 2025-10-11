package in.connectwithsandeepan.interviewgenius.userservice.controller;

import in.connectwithsandeepan.interviewgenius.userservice.dto.CreateOAuthUserRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.LinkOAuthProviderRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.LoginRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.UserResponse;
import in.connectwithsandeepan.interviewgenius.userservice.dto.ValidatePasswordRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.ValidatePasswordResponse;
import in.connectwithsandeepan.interviewgenius.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
@Slf4j
public class InternalAuthController {

    private final UserService userService;

    @PostMapping("/validate-password")
    public ResponseEntity<ValidatePasswordResponse> validatePassword(@RequestBody ValidatePasswordRequest request) {
        log.info("Validating password for email: {}", request.getEmail());
        boolean isValid = userService.validatePassword(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new ValidatePasswordResponse(isValid));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserResponse> authenticate(@RequestBody LoginRequest request) {
        log.info("Authenticating user with email: {}", request.getEmail());
        UserResponse response = userService.authenticateUser(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/oauth-user")
    public ResponseEntity<UserResponse> createOAuthUser(@RequestBody CreateOAuthUserRequest request) {
        log.info("Creating OAuth user for email: {} with provider: {}", request.getEmail(), request.getAuthProvider());
        UserResponse response = userService.createOAuthUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/link-provider")
    public ResponseEntity<UserResponse> linkOAuthProvider(@RequestBody LinkOAuthProviderRequest request) {
        log.info("Linking provider {} to user ID: {}", request.getAuthProvider(), request.getUserId());
        UserResponse response = userService.linkOAuthProvider(
            request.getUserId(),
            request.getAuthProvider(),
            request.getProviderUserId()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("Fetching user by email: {}", email);
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }
}
