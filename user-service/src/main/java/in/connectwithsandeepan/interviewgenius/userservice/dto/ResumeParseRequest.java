package in.connectwithsandeepan.interviewgenius.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for parsing resume text using AI service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeParseRequest {
    private String resumeText;
    private Long userId;
}
