package in.connectwithsandeepan.interviewgenius.interviewservice.client;

import in.connectwithsandeepan.interviewgenius.interviewservice.dto.InterviewResponseDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.dto.InterviewStartResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ai-service", path = "api/v1/ai")
public interface AiClient {

    @GetMapping("/transcribe")
    String transcribeAudio(@RequestParam("filePath") String filePath);

    @PostMapping("/interview/start")
    InterviewStartResponseDto startInterview(
            @RequestParam("conversationId") String conversationId,
            @RequestParam("experienceLevel") String experienceLevel,
            @RequestParam("language") String language);

    @PostMapping("/interview/answer")
    InterviewResponseDto submitAnswer(
            @RequestParam("conversationId") String conversationId,
            @RequestParam("answer") String answer);
}
