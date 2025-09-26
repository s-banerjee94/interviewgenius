package in.connectwithsandeepan.interviewgenius.aiservice.controller;

import in.connectwithsandeepan.interviewgenius.aiservice.entity.InputTypeQuestion;
import in.connectwithsandeepan.interviewgenius.aiservice.entity.Question;
import in.connectwithsandeepan.interviewgenius.aiservice.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class controller {
    private final AiService aiService;

    @GetMapping("/question")
    public Question question() {
        return aiService.genarateQuestion();
    }

    @GetMapping("/inputTypeQuestion")
    public InputTypeQuestion inputTypeQuestion() {
        return aiService.genarateInputTypeQuestion();
    }

    @GetMapping("/inputTypeQuestionSec")
    public InputTypeQuestion inputTypeQuestionSec() {
        return aiService.genarateInputTypeQuestionSen();
    }

    @GetMapping("/dsa")
    public InputTypeQuestion dsa() {
        return aiService.genarateInputTypeDsaQuestion();
    }

    @GetMapping("/transcribe")
    public ResponseEntity<String> transcribeAudio(@RequestParam String filePath) {
        try {
            String transcription = aiService.transcribeAudio(filePath);
            return ResponseEntity.ok(transcription);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing audio file: " + e.getMessage());
        }
    }
}
