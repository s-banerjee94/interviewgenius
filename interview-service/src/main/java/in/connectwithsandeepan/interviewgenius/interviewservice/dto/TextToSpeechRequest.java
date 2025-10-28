package in.connectwithsandeepan.interviewgenius.interviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextToSpeechRequest {
    private String text;

    /**
     * Voice options: alloy, echo, fable, onyx, nova, shimmer
     * Default: alloy
     */
    @Builder.Default
    private String voice = "alloy";
}
