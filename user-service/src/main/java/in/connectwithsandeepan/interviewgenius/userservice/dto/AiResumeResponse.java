package in.connectwithsandeepan.interviewgenius.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO to receive resume response from AI Service
 * AI Service returns skills as List<String>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiResumeResponse {

    @Builder.Default
    private List<AiWorkExperience> workExperiences = new ArrayList<>();

    @Builder.Default
    private List<AiEducation> educations = new ArrayList<>();

    @Builder.Default
    private List<String> skills = new ArrayList<>();  // AI service returns List<String>

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AiWorkExperience {
        private String jobTitle;
        private String companyName;
        private String location;
        private String startDate;
        private String endDate;
        private Boolean isCurrentRole;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AiEducation {
        private String degree;
        private String fieldOfStudy;
        private String institution;
        private String startDate;
        private String endDate;
        private Boolean isCurrentlyStudying;
        private String grade;
        private String description;
    }
}
