package in.connectwithsandeepan.interviewgenius.userservice.util;

import in.connectwithsandeepan.interviewgenius.userservice.dto.AiResumeResponse;
import in.connectwithsandeepan.interviewgenius.userservice.model.Education;
import in.connectwithsandeepan.interviewgenius.userservice.model.Resume;
import in.connectwithsandeepan.interviewgenius.userservice.model.Skill;
import in.connectwithsandeepan.interviewgenius.userservice.model.WorkExperience;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to convert AI service response to User service models
 */
public class ResumeConverter {

    /**
     * Convert AI service resume response to user service Resume model
     * Converts List<String> skills to List<Skill> with level as null
     *
     * @param aiResponse Response from AI service
     * @param userId User ID to associate with resume
     * @return User service Resume model
     */
    public static Resume convertToUserServiceResume(AiResumeResponse aiResponse, Long userId) {
        if (aiResponse == null) {
            return null;
        }

        Resume resume = Resume.builder()
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Convert work experiences
        if (aiResponse.getWorkExperiences() != null) {
            List<WorkExperience> workExperiences = aiResponse.getWorkExperiences().stream()
                    .map(ResumeConverter::convertWorkExperience)
                    .collect(Collectors.toList());
            resume.setWorkExperiences(workExperiences);
        } else {
            resume.setWorkExperiences(new ArrayList<>());
        }

        // Convert educations
        if (aiResponse.getEducations() != null) {
            List<Education> educations = aiResponse.getEducations().stream()
                    .map(ResumeConverter::convertEducation)
                    .collect(Collectors.toList());
            resume.setEducations(educations);
        } else {
            resume.setEducations(new ArrayList<>());
        }

        // Convert skills: List<String> -> List<Skill> with level as null
        if (aiResponse.getSkills() != null) {
            List<Skill> skills = aiResponse.getSkills().stream()
                    .map(skillName -> Skill.builder()
                            .name(skillName)
                            .level(null)  // Level is null as AI doesn't provide it
                            .build())
                    .collect(Collectors.toList());
            resume.setSkills(skills);
        } else {
            resume.setSkills(new ArrayList<>());
        }

        return resume;
    }

    /**
     * Convert AI work experience to user service model
     */
    private static WorkExperience convertWorkExperience(AiResumeResponse.AiWorkExperience aiWorkExp) {
        return WorkExperience.builder()
                .jobTitle(aiWorkExp.getJobTitle())
                .companyName(aiWorkExp.getCompanyName())
                .location(aiWorkExp.getLocation())
                .startDate(aiWorkExp.getStartDate())
                .endDate(aiWorkExp.getEndDate())
                .isCurrentRole(aiWorkExp.getIsCurrentRole())
                .description(aiWorkExp.getDescription())
                .build();
    }

    /**
     * Convert AI education to user service model
     */
    private static Education convertEducation(AiResumeResponse.AiEducation aiEducation) {
        return Education.builder()
                .degree(aiEducation.getDegree())
                .fieldOfStudy(aiEducation.getFieldOfStudy())
                .institution(aiEducation.getInstitution())
                .startDate(aiEducation.getStartDate())
                .endDate(aiEducation.getEndDate())
                .isCurrentlyStudying(aiEducation.getIsCurrentlyStudying())
                .grade(aiEducation.getGrade())
                .description(aiEducation.getDescription())
                .build();
    }
}
