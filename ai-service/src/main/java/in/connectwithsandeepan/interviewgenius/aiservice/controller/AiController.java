package in.connectwithsandeepan.interviewgenius.aiservice.controller;

import in.connectwithsandeepan.interviewgenius.aiservice.dto.InterviewResponse;
import in.connectwithsandeepan.interviewgenius.aiservice.dto.InterviewStartResponse;
import in.connectwithsandeepan.interviewgenius.aiservice.dto.ResumeParseRequest;
import in.connectwithsandeepan.interviewgenius.aiservice.entity.InputTypeQuestion;
import in.connectwithsandeepan.interviewgenius.aiservice.entity.Question;
import in.connectwithsandeepan.interviewgenius.aiservice.entity.Resume;
import in.connectwithsandeepan.interviewgenius.aiservice.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiService aiService;

    @GetMapping("/question")
    public Question question() {
        return aiService.genarateQuestion();
    }

    @GetMapping("/shortInputTypeQuestion")
    public InputTypeQuestion getShortInputTypeQuestion() {
        return aiService.generateShortInputTypeQuestion();
    }

    @GetMapping("/descriptiveInputTypeQuestion")
    public InputTypeQuestion getDescriptiveInputTypeQuestion() {
        InputTypeQuestion inputTypeQuestion = aiService.generateDescriptiveInputTypeQuestion();
        return inputTypeQuestion;
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

    @PostMapping("/interview/start")
    public ResponseEntity<InterviewStartResponse> startInterview(
            @RequestParam String conversationId,
            @RequestParam String experienceLevel,
            @RequestParam String language) {
        String firstQuestion = aiService.startInterview(conversationId, experienceLevel, language);
        return ResponseEntity.ok(InterviewStartResponse.builder()
                .question(firstQuestion)
                .build());
    }

    @PostMapping("/interview/answer")
    public ResponseEntity<InterviewResponse> submitAnswer(
            @RequestParam String conversationId,
            @RequestParam String answer) {
        InterviewResponse response = aiService.submitAnswerAndGetNextQuestion(conversationId, answer);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/parse-resume")
    public ResponseEntity<Resume> parseResume(@RequestBody ResumeParseRequest request) {
        log.info("Received resume parse request for userId: {}", request.getUserId());

        try {
            if (request.getResumeText() == null || request.getResumeText().trim().isEmpty()) {
                log.error("Resume text is empty");
                return ResponseEntity.badRequest().build();
            }

            Resume resume = aiService.parseResumeText(request.getResumeText(), request.getUserId());
            log.info("Resume parsed successfully for userId: {}", request.getUserId());

            return ResponseEntity.ok(resume);

        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error parsing resume: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
