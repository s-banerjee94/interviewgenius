package in.connectwithsandeepan.interviewgenius.interviewservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextToSpeechResponse {
    private String filePath;
    private Long fileSizeBytes;
}
