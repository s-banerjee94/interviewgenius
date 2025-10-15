package in.connectwithsandeepan.interviewgenius.userservice.controller;

import in.connectwithsandeepan.interviewgenius.userservice.client.AiServiceClient;
import in.connectwithsandeepan.interviewgenius.userservice.dto.*;
import in.connectwithsandeepan.interviewgenius.userservice.entity.User;
import in.connectwithsandeepan.interviewgenius.userservice.model.Resume;
import in.connectwithsandeepan.interviewgenius.userservice.service.UserService;
import in.connectwithsandeepan.interviewgenius.userservice.util.PdfParserUtil;
import in.connectwithsandeepan.interviewgenius.userservice.util.ResumeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    private final PdfParserUtil pdfParserUtil;
    private final AiServiceClient aiServiceClient;

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
    public ResponseEntity<UserResponse> updateUser(Long id, UpdateUserRequest updateUserRequest) {
        UserResponse user = userService.updateUser(id, updateUserRequest);
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

    // User can update their own resume, Admin can update any resume
    @Override
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    public ResponseEntity<Resume> updateResume(Long id, Resume resume) {
        Resume updatedResume = userService.updateResume(id, resume);
        return ResponseEntity.ok(updatedResume);
    }

    // Public endpoint - upload and parse PDF resume file
    @Override
    public ResponseEntity<Resume> uploadPdf(MultipartFile file, Long userId) {
        log.info("Received PDF resume upload request: {}", file.getOriginalFilename());

        // Validate userId
        if (userId == null) {
            log.error("userId is required");
            return ResponseEntity.badRequest().build();
        }

        // Validate file
        if (file.isEmpty()) {
            log.error("Uploaded file is empty");
            return ResponseEntity.badRequest().build();
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            log.error("Invalid file type: {}", contentType);
            return ResponseEntity.badRequest().build();
        }

        try {
            // Extract text from PDF
            log.info("Extracting text from PDF: {}", file.getOriginalFilename());
            String extractedText = pdfParserUtil.parsePdfAndPrint(file);

            if (extractedText == null || extractedText.trim().isEmpty()) {
                log.error("No text extracted from PDF");
                return ResponseEntity.badRequest().build();
            }

            log.info("Text extracted successfully. Length: {} characters", extractedText.length());

            // Call AI service to parse resume text into structured format
            log.info("Calling AI service to parse resume text");
            ResumeParseRequest parseRequest = ResumeParseRequest.builder()
                    .resumeText(extractedText)
                    .userId(userId)
                    .build();

            ResponseEntity<AiResumeResponse> aiResponse = aiServiceClient.parseResume(parseRequest);

            if (aiResponse.getStatusCode().is2xxSuccessful() && aiResponse.getBody() != null) {
                AiResumeResponse aiResumeResponse = aiResponse.getBody();
                Resume userServiceResume = ResumeConverter.convertToUserServiceResume(aiResumeResponse, userId);
                log.info("Resume parsed and converted successfully - Work Experiences: {}, Education: {}, Skills: {}",
                        userServiceResume.getWorkExperiences().size(),
                        userServiceResume.getEducations().size(),
                        userServiceResume.getSkills().size());

                // Save resume to user entity in MySQL
                Resume savedResume = userService.updateResume(userId, userServiceResume);
                log.info("Resume saved to user entity in database for userId: {}", userId);

                return ResponseEntity.ok(savedResume);
            } else {
                log.error("AI service returned unsuccessful response");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        } catch (IOException e) {
            log.error("Error processing PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Error calling AI service: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Resume> getResume(Long id) {
        log.info("Getting resume for user: {}", id);
        Resume resume = userService.getResumeById(id);
        return ResponseEntity.ok(resume);
    }
}