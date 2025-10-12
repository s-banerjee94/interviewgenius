package in.connectwithsandeepan.interviewgenius.userservice.service;

import in.connectwithsandeepan.interviewgenius.userservice.dto.CreateOAuthUserRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.UserRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.UserResponse;
import in.connectwithsandeepan.interviewgenius.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface UserService {

    UserResponse createUser(UserRequest userRequest);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    Page<UserResponse> getAllUsers(Pageable pageable);

    Page<UserResponse> getUsersByRole(User.Role role, Pageable pageable);

    Page<UserResponse> getUsersByStatus(Boolean isActive, Pageable pageable);

    Page<UserResponse> getUsersWithFilters(
        String firstName,
        String lastName,
        String email,
        User.Role role,
        Boolean isActive,
        Pageable pageable
    );

    UserResponse updateUser(Long id, UserRequest userRequest);

    UserResponse updateUserStatus(Long id, Boolean isActive);

    UserResponse verifyUser(Long id);

    void deleteUser(Long id);

    boolean existsByEmail(String email);

    long getTotalUserCount();

    long getUserCountByRole(User.Role role);

    UserResponse loginUser(String email, String password);

    void changePassword(Long userId, String oldPassword, String newPassword);

    // Internal auth methods
    boolean validatePassword(String email, String password);

    UserResponse authenticateUser(String email, String password);

    UserResponse createOAuthUser(CreateOAuthUserRequest request);

    UserResponse linkOAuthProvider(Long userId, User.AuthProvider authProvider);
}