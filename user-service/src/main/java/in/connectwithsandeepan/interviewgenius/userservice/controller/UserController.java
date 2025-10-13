package in.connectwithsandeepan.interviewgenius.userservice.controller;

import in.connectwithsandeepan.interviewgenius.userservice.dto.ChangePasswordRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.LoginRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.PdfUploadResponse;
import in.connectwithsandeepan.interviewgenius.userservice.dto.UpdateUserRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.UserRequest;
import in.connectwithsandeepan.interviewgenius.userservice.dto.UserResponse;
import in.connectwithsandeepan.interviewgenius.userservice.dto.UserStatsResponse;
import in.connectwithsandeepan.interviewgenius.userservice.entity.User;
import in.connectwithsandeepan.interviewgenius.userservice.service.UserService;
import in.connectwithsandeepan.interviewgenius.userservice.util.PdfParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;
    private final PdfParserUtil pdfParserUtil;

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
    public ResponseEntity<in.connectwithsandeepan.interviewgenius.userservice.model.Resume> updateResume(Long id, in.connectwithsandeepan.interviewgenius.userservice.model.Resume resume) {
        in.connectwithsandeepan.interviewgenius.userservice.model.Resume updatedResume = userService.updateResume(id, resume);
        return ResponseEntity.ok(updatedResume);
    }

    // Public endpoint - upload and parse PDF file
    @Override
    public ResponseEntity<PdfUploadResponse> uploadPdf(MultipartFile file) {
        log.info("Received PDF upload request: {}", file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            log.error("Uploaded file is empty");
            return ResponseEntity.badRequest()
                    .body(PdfUploadResponse.builder()
                            .message("File is empty")
                            .build());
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            log.error("Invalid file type: {}", contentType);
            return ResponseEntity.badRequest()
                    .body(PdfUploadResponse.builder()
                            .fileName(file.getOriginalFilename())
                            .message("Invalid file type. Only PDF files are allowed")
                            .build());
        }

        try {
            // Get page count
            int pageCount;
            try (InputStream inputStream = file.getInputStream();
                 PDDocument document = PDDocument.load(inputStream)) {
                pageCount = document.getNumberOfPages();
            }

            // Parse PDF and extract text (this also prints to console)
            String extractedText = pdfParserUtil.parsePdfAndPrint(file);

            // Build response
            PdfUploadResponse response = PdfUploadResponse.builder()
                    .fileName(file.getOriginalFilename())
                    .fileSizeBytes(file.getSize())
                    .pageCount(pageCount)
                    .characterCount(extractedText.length())
                    .extractedText(extractedText)
                    .message("PDF parsed successfully")
                    .build();

            log.info("PDF upload and parsing completed successfully");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("Error processing PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PdfUploadResponse.builder()
                            .fileName(file.getOriginalFilename())
                            .message("Error processing PDF: " + e.getMessage())
                            .build());
        }
    }
}