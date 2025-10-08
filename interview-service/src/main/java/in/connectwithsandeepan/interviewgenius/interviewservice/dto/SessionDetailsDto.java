package in.connectwithsandeepan.interviewgenius.interviewservice.dto;

import in.connectwithsandeepan.interviewgenius.interviewservice.entity.InterviewSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDetailsDto {
    private String sessionId;
    private String userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private InterviewSession.Status status;
    private List<QuestionAnswerDto> questionAnswers;
}