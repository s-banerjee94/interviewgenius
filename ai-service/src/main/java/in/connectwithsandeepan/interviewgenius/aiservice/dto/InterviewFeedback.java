package in.connectwithsandeepan.interviewgenius.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewFeedback {
    private String feedback;
    private Integer score;
}
