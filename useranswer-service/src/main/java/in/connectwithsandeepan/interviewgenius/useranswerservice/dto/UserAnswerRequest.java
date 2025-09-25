package in.connectwithsandeepan.interviewgenius.useranswerservice.dto;

import lombok.Data;

@Data
public class UserAnswerRequest {
    private String userId;
    private String questionId;
    private String givenAnswer;
    private Boolean correct;
}