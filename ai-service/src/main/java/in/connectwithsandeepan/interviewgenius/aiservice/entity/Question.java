package in.connectwithsandeepan.interviewgenius.aiservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    private String question;
    private String answer;
    private List<String> options;
    private String programingLanguage;
    private String difficulty;
}

