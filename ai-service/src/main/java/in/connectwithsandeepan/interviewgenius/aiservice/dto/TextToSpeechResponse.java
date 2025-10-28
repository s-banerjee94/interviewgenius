package in.connectwithsandeepan.interviewgenius.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for text to speech conversion
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextToSpeechResponse {
    private String filePath;
    private Long fileSizeBytes;
}
