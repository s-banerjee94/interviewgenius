package in.connectwithsandeepan.interviewgenius.userservice.service.impl;

import in.connectwithsandeepan.interviewgenius.userservice.dto.CreateOAuthUserRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.UserRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.UserResponse;
import in.connectwithsandeepan.interviewgenius.userservice.entity.User;
import in.connectwithsandeepan.interviewgenius.userservice.exception.EmailAlreadyInUseException;
import in.connectwithsandeepan.interviewgenius.userservice.exception.InvalidPasswordException;
import in.connectwithsandeepan.interviewgenius.userservice.exception.UserAlreadyExistsException;
import in.connectwithsandeepan.interviewgenius.userservice.exception.UserNotFoundException;
import in.connectwithsandeepan.interviewgenius.userservice.repository.UserRepository;
import in.connectwithsandeepan.interviewgenius.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        if (existsByEmail(userRequest.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + userRequest.getEmail() + " already exists");
        }

        HashSet<User.AuthProvider> providers = new HashSet<>();
        providers.add(User.AuthProvider.LOCAL);

        User user = User.builder()
            .email(userRequest.getEmail())
            .firstName(userRequest.getFirstName())
            .lastName(userRequest.getLastName())
            .password(passwordEncoder.encode(userRequest.getPassword()))
            .authProviders(providers)
            .role(userRequest.getRole() != null ? userRequest.getRole() : User.Role.USER)
            .phoneNumber(userRequest.getPhoneNumber())
            .isActive(true)
            .isVerified(false)
            .build();

        User savedUser = userRepository.save(user);
        log.info("Created new user with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());

        return UserResponse.fromUser(savedUser);
    }

    @Deprecated
    @Override
    public UserResponse loginUser(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new UserNotFoundException("email", email));

        if (!user.getPassword().equals(password)) { // TODO: Use password encoder when security is added
            throw new InvalidPasswordException();
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return UserResponse.fromUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        return UserResponse.fromUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("email", email));
        return UserResponse.fromUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(UserResponse::fromUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByRole(User.Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
            .map(UserResponse::fromUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByStatus(Boolean isActive, Pageable pageable) {
        return userRepository.findByIsActive(isActive, pageable)
            .map(UserResponse::fromUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersWithFilters(
        String firstName, String lastName, String email,
        User.Role role, Boolean isActive, Pageable pageable) {

        return userRepository.findUsersWithFilters(
            firstName, lastName, email, role, isActive, pageable
        ).map(UserResponse::fromUser);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        if (!user.getEmail().equals(userRequest.getEmail()) && existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyInUseException("Email " + userRequest.getEmail() + " is already in use");
        }

        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setExperience(userRequest.getExperience());

        User updatedUser = userRepository.save(user);
        log.info("Updated user with ID: {}", updatedUser.getId());

        return UserResponse.fromUser(updatedUser);
    }

    @Override
    public UserResponse updateUserStatus(Long id, Boolean isActive) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        user.setIsActive(isActive);
        User updatedUser = userRepository.save(user);
        log.info("Updated user status for ID: {} to {}", id, isActive);

        return UserResponse.fromUser(updatedUser);
    }

    @Override
    public UserResponse verifyUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        user.setIsVerified(true);
        User updatedUser = userRepository.save(user);
        log.info("Verified user with ID: {}", id);

        return UserResponse.fromUser(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        log.info("Deleted user with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUserCountByRole(User.Role role) {
        return userRepository.countByRole(role);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        // Verify old password matches
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException("Old password is incorrect");
        }

        // Update to new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed successfully for user ID: {}", userId);
    }

    // Internal auth methods
    @Override
    public boolean validatePassword(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        if (user.getPassword() == null) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public UserResponse authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidPasswordException("Invalid email or password"));

        if (user.getPassword() == null) {
            throw new InvalidPasswordException("Invalid email or password");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException("Invalid email or password");
        }

        user.setLastLoginAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        log.info("User authenticated successfully: {}", email);

        return UserResponse.fromUser(updatedUser);
    }

    @Override
    public UserResponse createOAuthUser(CreateOAuthUserRequest request) {
        // Try to find existing user first (idempotent operation)
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            log.info("User with email {} already exists. Linking OAuth provider if needed.", request.getEmail());

            // Check if provider is already linked
            if (!user.getAuthProviders().contains(request.getAuthProvider())) {
                // Link the OAuth provider
                user.getAuthProviders().add(request.getAuthProvider());

                if (request.getProviderUserId() != null) {
                    user.getOauthProviderIds().put(
                        request.getAuthProvider().name().toLowerCase(),
                        request.getProviderUserId()
                    );
                }

                user = userRepository.save(user);
                log.info("Linked {} provider to existing user ID: {}", request.getAuthProvider(), user.getId());
            } else {
                log.info("Provider {} already linked to user ID: {}", request.getAuthProvider(), user.getId());
            }

            return UserResponse.fromUser(user);
        }

        // User doesn't exist, create new one
        HashSet<User.AuthProvider> providers = new HashSet<>();
        providers.add(request.getAuthProvider());

        User user = User.builder()
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .password(null) // OAuth users don't have password
            .authProviders(providers)
            .role(User.Role.USER)
            .isActive(true)
            .isVerified(true) // OAuth users are pre-verified
            .profileImageUrl(request.getProfileImageUrl())
            .build();

        if (request.getProviderUserId() != null) {
            user.getOauthProviderIds().put(
                request.getAuthProvider().name().toLowerCase(),
                request.getProviderUserId()
            );
        }

        User savedUser = userRepository.save(user);
        log.info("Created new OAuth user with ID: {} and email: {} via provider: {}",
            savedUser.getId(), savedUser.getEmail(), request.getAuthProvider());

        return UserResponse.fromUser(savedUser);
    }

    @Override
    public UserResponse linkOAuthProvider(Long userId, User.AuthProvider authProvider, String providerUserId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        // Check if provider is already linked (idempotent operation)
        if (user.getAuthProviders().contains(authProvider)) {
            log.info("Provider {} already linked to user ID: {}. Returning existing user.", authProvider, userId);
            return UserResponse.fromUser(user);
        }

        // Link the provider
        user.getAuthProviders().add(authProvider);
        if (providerUserId != null) {
            user.getOauthProviderIds().put(authProvider.name().toLowerCase(), providerUserId);
        }

        User updatedUser = userRepository.save(user);
        log.info("Linked {} provider to user ID: {}", authProvider, userId);

        return UserResponse.fromUser(updatedUser);
    }

}