package in.connectwithsandeepan.interviewgenius.useranswerservice.controller;

import in.connectwithsandeepan.interviewgenius.useranswerservice.dto.UserAnswerRequest;
import in.connectwithsandeepan.interviewgenius.useranswerservice.dto.UserAnswerResponse;
import in.connectwithsandeepan.interviewgenius.useranswerservice.service.UserAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user-answers")
@RequiredArgsConstructor
public class UserAnswerController {

    private final UserAnswerService userAnswerService;

    @PostMapping
    public ResponseEntity<UserAnswerResponse> submitAnswer(@RequestBody UserAnswerRequest request) {
        UserAnswerResponse response = userAnswerService.submitAnswer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAnswerResponse> getUserAnswerById(@PathVariable String id) {
        UserAnswerResponse response = userAnswerService.getUserAnswerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserAnswersByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "false") boolean paginated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (paginated) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedAt"));
            Page<UserAnswerResponse> responses = userAnswerService.getUserAnswersByUserId(userId, pageable);
            return ResponseEntity.ok(responses);
        }
        List<UserAnswerResponse> responses = userAnswerService.getUserAnswersByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<?> getUserAnswersByQuestionId(
            @PathVariable String questionId,
            @RequestParam(defaultValue = "false") boolean paginated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (paginated) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedAt"));
            Page<UserAnswerResponse> responses = userAnswerService.getUserAnswersByQuestionId(questionId, pageable);
            return ResponseEntity.ok(responses);
        }
        List<UserAnswerResponse> responses = userAnswerService.getUserAnswersByQuestionId(questionId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}/question/{questionId}")
    public ResponseEntity<List<UserAnswerResponse>> getUserAnswersByUserIdAndQuestionId(
            @PathVariable String userId,
            @PathVariable String questionId) {

        List<UserAnswerResponse> responses = userAnswerService.getUserAnswersByUserIdAndQuestionId(userId, questionId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<Page<UserAnswerResponse>> getAllUserAnswers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submittedAt"));
        Page<UserAnswerResponse> responses = userAnswerService.getAllUserAnswers(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<UserAnswerResponse>> getRecentAnswers(
            @RequestParam(defaultValue = "10") int limit) {

        List<UserAnswerResponse> responses = userAnswerService.getRecentAnswers(limit);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserAnswer(@PathVariable String id) {
        userAnswerService.deleteUserAnswer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAnswers", userAnswerService.getTotalAnswerCount());
        stats.put("correctAnswers", userAnswerService.getCorrectAnswerCount());
        stats.put("incorrectAnswers", userAnswerService.getIncorrectAnswerCount());
        stats.put("accuracyPercentage", userAnswerService.getAccuracyPercentage());
        return ResponseEntity.ok(stats);
    }
}