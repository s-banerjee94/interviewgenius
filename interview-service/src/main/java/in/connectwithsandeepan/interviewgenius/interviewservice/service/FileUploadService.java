package in.connectwithsandeepan.interviewgenius.interviewservice.service;

import in.connectwithsandeepan.interviewgenius.interviewservice.dto.UploadResponseDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.exception.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class FileUploadService {

    private static final List<String> ALLOWED_AUDIO_EXTENSIONS = Arrays.asList(
            "mp3", "wav", "m4a", "webm", "ogg", "aac", "flac", "wma"
    );

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "audio/mpeg", "audio/wav", "audio/mp4", "audio/webm",
            "audio/ogg", "audio/aac", "audio/flac", "audio/x-ms-wma"
    );

    public UploadResponseDto uploadFile(MultipartFile file, String userId, String email, String sessionId) {
        try {
            if (file.isEmpty()) {
                throw new FileUploadException("Cannot upload empty file");
            }

            String uploadDir = System.getProperty("user.dir") + "/uploads/" + userId + "_" + email + "/" + sessionId + "/";

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath);
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new FileUploadException("Invalid file name");
            }

            validateAudioFile(file, fileName);

            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());
            log.info("File uploaded successfully: {}", filePath);

            return new UploadResponseDto(
                    userId,
                    sessionId,
                    filePath.toString()
            );
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new FileUploadException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    private void validateAudioFile(MultipartFile file, String fileName) {
        // Validate file extension
        String fileExtension = getFileExtension(fileName);
        if (!ALLOWED_AUDIO_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new FileUploadException(
                    String.format("Invalid file type. Allowed audio formats: %s. Received: %s",
                            String.join(", ", ALLOWED_AUDIO_EXTENSIONS), fileExtension)
            );
        }

        // Validate content type
        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            log.warn("File content type {} does not match standard audio types", contentType);
        }

        log.info("File validation passed: {} ({})", fileName, contentType);
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            throw new FileUploadException("File must have a valid extension");
        }
        return fileName.substring(lastDotIndex + 1);
    }
}