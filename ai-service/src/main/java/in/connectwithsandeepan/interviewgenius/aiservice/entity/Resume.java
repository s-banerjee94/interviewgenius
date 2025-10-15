package in.connectwithsandeepan.interviewgenius.aiservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's resume stored as JSON
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {
    @Builder.Default
    private List<WorkExperience> workExperiences = new ArrayList<>();

    @Builder.Default
    private List<Education> educations = new ArrayList<>();

    @Builder.Default
    private List<String> skills = new ArrayList<>();

}
