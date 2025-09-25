package in.connectwithsandeepan.interviewgenius.useranswerservice.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "user_answers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAnswer {
    @Id
    private String id;

    private String userId;       // Reference to User
    private String questionId;   // Reference to Question

    private String givenAnswer;  // User’s answer (text or option)
    private Boolean correct;     // Optional: auto-evaluated for MCQ
    private LocalDateTime submittedAt;
}
