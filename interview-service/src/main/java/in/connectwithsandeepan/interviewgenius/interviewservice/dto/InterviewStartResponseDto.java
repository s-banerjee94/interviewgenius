package in.connectwithsandeepan.interviewgenius.interviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewStartResponseDto {
    private String question;
}
