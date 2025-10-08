package in.connectwithsandeepan.interviewgenius.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Error response structure")
public class ErrorResponseDto {
    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Error type", example = "User Not Found")
    private String error;

    @Schema(description = "Detailed error message", example = "User not found with ID: 123")
    private String message;

    @Schema(description = "Request path", example = "/api/v1/users/123")
    private String path;

    @Schema(description = "Error timestamp", example = "2025-10-08 12:30:45")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
