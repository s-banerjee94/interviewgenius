package in.connectwithsandeepan.interviewgenius.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PdfUploadResponse {
    private String fileName;
    private Long fileSizeBytes;
    private Integer pageCount;
    private Integer characterCount;
    private String extractedText;
    private String message;
}
