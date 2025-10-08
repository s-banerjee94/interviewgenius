package in.connectwithsandeepan.interviewgenius.interviewservice.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "interview_sessions")
@CompoundIndex(name = "userId_status_idx", def = "{'userId': 1, 'status': 1}")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewSession {
    @Id
    private String id;

    @Indexed(name = "userId_idx")
    private String userId;

    @Indexed(name = "startTime_idx")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Indexed(name = "status_idx")
    private Status status;

    private List<QuestionAnswer> questionAnswers;
    private String experienceLevel;
    private String language;

    public enum Status {
        ACTIVE, COMPLETED
    }
}
