package in.connectwithsandeepan.interviewgenius.interviewservice.controller;


import in.connectwithsandeepan.interviewgenius.interviewservice.dto.AnswerSubmissionResponseDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.dto.QuestionDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.dto.SessionDetailsDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.dto.SessionListDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.dto.UploadResponseDto;
import in.connectwithsandeepan.interviewgenius.interviewservice.entity.InterviewSession;
import in.connectwithsandeepan.interviewgenius.interviewservice.service.FileUploadService;
import in.connectwithsandeepan.interviewgenius.interviewservice.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
@Validated
public class InterviewController implements InterviewControllerApi {
    private final InterviewService interviewService;
    private final FileUploadService fileUploadService;


    @Override
    @PostMapping("/start/{userId}")
    public ResponseEntity<InterviewSession> start(
            @PathVariable String userId,
            @RequestParam String experienceLevel,
            @RequestParam String language) {
        return ResponseEntity.ok(interviewService.startSession(userId, experienceLevel, language));
    }

    @Override
    @GetMapping("/{sessionId}/question")
    public ResponseEntity<QuestionDto> getFirstQuestion(
            @PathVariable String sessionId) {
        QuestionDto question = interviewService.getFirstQuestion(sessionId);
        return ResponseEntity.ok(question);
    }


    @Override
    @PostMapping("/{sessionId}/end")
    public ResponseEntity<InterviewSession> end(
            @PathVariable String sessionId,
            @RequestParam boolean force) {
        return ResponseEntity.ok(interviewService.endSession(sessionId, force));
    }

    @Override
    @GetMapping("/{sessionId}/details")
    public ResponseEntity<SessionDetailsDto> getSessionDetails(
            @PathVariable String sessionId) {
        SessionDetailsDto sessionDetails = interviewService.getSessionDetails(sessionId);
        return ResponseEntity.ok(sessionDetails);
    }

    @Override
    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<AnswerSubmissionResponseDto> submitAnswer(
            @PathVariable String sessionId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId,
            @RequestParam("userName") String userName) {
        UploadResponseDto uploadResponse = fileUploadService.uploadFile(file, userId, userName, sessionId);
        AnswerSubmissionResponseDto response = interviewService.submitAnswer(sessionId, uploadResponse.getUploadFileLocation());
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/sessions/{userId}")
    public ResponseEntity<List<SessionListDto>> getAllSessions(
            @PathVariable String userId,
            @RequestParam(defaultValue = "false") boolean pagination,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<SessionListDto> sessions = interviewService.getAllSessions(userId, pagination, page, size);
        return ResponseEntity.ok(sessions);
    }


}
