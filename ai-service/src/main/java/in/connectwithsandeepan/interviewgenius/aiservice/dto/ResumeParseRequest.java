package in.connectwithsandeepan.interviewgenius.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for parsing resume text using AI
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeParseRequest {
    private String resumeText;
    private Long userId; // Optional: to associate with a specific user
}
