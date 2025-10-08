package in.connectwithsandeepan.interviewgenius.interviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponseDto {
    private String userId;
    private String sessionId;
    private String uploadFileLocation;
}