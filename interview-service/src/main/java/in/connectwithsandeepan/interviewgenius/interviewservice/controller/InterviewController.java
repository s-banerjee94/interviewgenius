package in.connectwithsandeepan.interviewgenius.interviewservice.controller;


import in.connectwithsandeepan.interviewgenius.interviewservice.dto.AnswerSubmissionResponseDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.dto.QuestionDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.dto.SessionDetailsDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.dto.UploadResponseDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.entity.InterviewSession;
import in.connectwithsandeepan.interviewgenius.interviewservice.service.FileUploadService;
import in.connectwithsandeepan.interviewgenius.interviewservice.service.InterviewService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
@Validated
public class InterviewController {
    private final InterviewService interviewService;
    private final FileUploadService fileUploadService;


    @PostMapping("/start/{userId}")
    public ResponseEntity<InterviewSession> start(
            @PathVariable @NotBlank(message = "User ID cannot be empty") String userId,
            @RequestParam @NotBlank(message = "Experience level cannot be empty") String experienceLevel,
            @RequestParam @NotBlank(message = "Language cannot be empty") String language) {
        return ResponseEntity.ok(interviewService.startSession(userId, experienceLevel, language));
    }

    @GetMapping("/{sessionId}/question")
    public ResponseEntity<QuestionDto> getFirstQuestion(
            @PathVariable @NotBlank(message = "Session ID cannot be empty") String sessionId) {
        QuestionDto question = interviewService.getFirstQuestion(sessionId);
        return ResponseEntity.ok(question);
    }


    @PostMapping("/{sessionId}/end")
    public ResponseEntity<InterviewSession> end(
            @PathVariable @NotBlank(message = "Session ID cannot be empty") String sessionId,
            @RequestParam boolean force) {
        return ResponseEntity.ok(interviewService.endSession(sessionId, force));
    }

    @GetMapping("/{sessionId}/details")
    public ResponseEntity<SessionDetailsDto> getSessionDetails(
            @PathVariable @NotBlank(message = "Session ID cannot be empty") String sessionId) {
        SessionDetailsDto sessionDetails = interviewService.getSessionDetails(sessionId);
        return ResponseEntity.ok(sessionDetails);
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<AnswerSubmissionResponseDto> submitAnswer(
            @PathVariable @NotBlank(message = "Session ID cannot be empty") String sessionId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") @NotBlank(message = "User ID cannot be empty") String userId,
            @RequestParam("userName") @NotBlank(message = "User name cannot be empty") String userName) {
        UploadResponseDto uploadResponse = fileUploadService.uploadFile(file, userId, userName, sessionId);
        AnswerSubmissionResponseDto response = interviewService.submitAnswer(sessionId, uploadResponse.getUploadFileLocation());
        return ResponseEntity.ok(response);
    }


}
