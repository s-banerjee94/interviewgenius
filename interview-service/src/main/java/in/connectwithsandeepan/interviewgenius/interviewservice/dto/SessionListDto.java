package in.connectwithsandeepan.interviewgenius.interviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionListDto {
    private String sessionId;
    private String language;
    private LocalDateTime startTime;
    private String experienceLevel;
}
