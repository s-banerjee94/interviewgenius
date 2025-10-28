package in.connectwithsandeepan.interviewgenius.interviewservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerSubmissionResponseDto {
    private Integer questionIndex;
    private String question;
    private String answer;
    private Integer totalQuestionsAnswered;
    private String sessionStatus;
    private FeedbackDto feedback;
    private String nextQuestion;
    private String audioBase64;
}
