package in.connectwithsandeepan.interviewgenius.useranswerservice.dto;

import in.connectwithsandeepan.interviewgenius.useranswerservice.entity.UserAnswer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAnswerResponse {
    private String id;
    private String userId;
    private String questionId;
    private String givenAnswer;
    private Boolean correct;
    private LocalDateTime submittedAt;

    public static UserAnswerResponse fromUserAnswer(UserAnswer userAnswer) {
        UserAnswerResponse response = new UserAnswerResponse();
        response.setId(userAnswer.getId());
        response.setUserId(userAnswer.getUserId());
        response.setQuestionId(userAnswer.getQuestionId());
        response.setGivenAnswer(userAnswer.getGivenAnswer());
        response.setCorrect(userAnswer.getCorrect());
        response.setSubmittedAt(userAnswer.getSubmittedAt());
        return response;
    }
}