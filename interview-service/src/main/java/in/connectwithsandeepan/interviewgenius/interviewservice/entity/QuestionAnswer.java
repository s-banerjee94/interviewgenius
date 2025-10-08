package in.connectwithsandeepan.interviewgenius.interviewservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswer {
    private Integer questionIndex;
    private String question;
    private String answer;
    private String audioFileUrl;
    private LocalDateTime questionTimestamp;
    private LocalDateTime answerTimestamp;
    private String feedback;
    private Integer score;
}
