package in.connectwithsandeepan.interviewgenius.userservice.controller;

import in.connectwithsandeepan.interviewgenius.userservice.dto.ChangePasswordRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.LoginRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.UserRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.UserResponse;
import in.connectwithsandeepan.interviewgenius.userservice.dto.UserStatsResponse;
import in.connectwithsandeepan.interviewgenius.userservice.entity.User;
import in.connectwithsandeepan.interviewgenius.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    // Public endpoint - anyone can register
    @Override
    public ResponseEntity<UserResponse> createUser(UserRequest userRequest) {
        UserResponse user = userService.createUser(userRequest);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    // Public endpoint - anyone can login
    @Override
    public ResponseEntity<UserResponse> loginUser(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        UserResponse user = userService.loginUser(email, password);
        return ResponseEntity.ok(user);
    }

    // User can view their own profile, Admin can view any profile
    @Override
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    public ResponseEntity<UserResponse> getUserById(Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // User can view their own profile, Admin can view any profile
    @Override
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.principal.email")
    public ResponseEntity<UserResponse> getUserByEmail(String email) {
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    // Admin only - list all users
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    // Admin only - filter users by role
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUsersByRole(User.Role role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> users = userService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(users);
    }

    // Admin only - filter users by status
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUsersByStatus(Boolean isActive, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> users = userService.getUsersByStatus(isActive, pageable);
        return ResponseEntity.ok(users);
    }

    // Admin only - search users with filters
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            String firstName, String lastName, String email,
            User.Role role, Boolean isActive, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> users = userService.getUsersWithFilters(
            firstName, lastName, email, role, isActive, pageable);
        return ResponseEntity.ok(users);
    }

    // User can update their own profile, Admin can update any profile
    @Override
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    public ResponseEntity<UserResponse> updateUser(Long id, UserRequest userRequest) {
        UserResponse user = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(user);
    }

    // Admin only - activate/deactivate users
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserStatus(Long id, Boolean isActive) {
        UserResponse user = userService.updateUserStatus(id, isActive);
        return ResponseEntity.ok(user);
    }

    // Admin only - verify users
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> verifyUser(Long id) {
        UserResponse user = userService.verifyUser(id);
        return ResponseEntity.ok(user);
    }

    // Admin only - delete users
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Public endpoint - check if email exists (for registration validation)
    @Override
    public ResponseEntity<Boolean> existsByEmail(String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    // Admin only - view user statistics
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserStatsResponse> getUserStats() {
        UserStatsResponse stats = new UserStatsResponse(
            userService.getTotalUserCount(),
            userService.getUserCountByRole(User.Role.ADMIN),
            userService.getUserCountByRole(User.Role.USER)
        );
        return ResponseEntity.ok(stats);
    }

    // User can change their own password, Admin can change any password
    @Override
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    public ResponseEntity<Void> changePassword(Long id, ChangePasswordRequest request) {
        userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}