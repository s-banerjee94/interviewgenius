package in.connectwithsandeepan.interviewgenius.questionservice.controller;

import in.connectwithsandeepan.interviewgenius.questionservice.entity.Question;
import in.connectwithsandeepan.interviewgenius.questionservice.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/generate")
    public ResponseEntity<Question> generateAndSaveQuestion() {
        Question question = questionService.generateAndSaveQuestionFromAi();
        return ResponseEntity.status(HttpStatus.CREATED).body(question);
    }

    @GetMapping
    public ResponseEntity<?> getAllQuestions(
            @RequestParam(defaultValue = "false") boolean paginated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (paginated) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
            return ResponseEntity.ok(questionService.getAllQuestions(pageable));
        }
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/by-language")
    public ResponseEntity<?> getQuestionsByLanguage(
            @RequestParam String language,
            @RequestParam(defaultValue = "false") boolean paginated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (paginated) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
            return ResponseEntity.ok(questionService.getQuestionsByLanguage(language, pageable));
        }
        return ResponseEntity.ok(questionService.getQuestionsByLanguage(language));
    }

    @GetMapping("/by-difficulty")
    public ResponseEntity<?> getQuestionsByDifficulty(
            @RequestParam String difficulty,
            @RequestParam(defaultValue = "false") boolean paginated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (paginated) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
            return ResponseEntity.ok(questionService.getQuestionsByDifficulty(difficulty, pageable));
        }
        return ResponseEntity.ok(questionService.getQuestionsByDifficulty(difficulty));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getQuestionsByLanguageAndDifficulty(
            @RequestParam String language,
            @RequestParam String difficulty,
            @RequestParam(defaultValue = "false") boolean paginated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (paginated) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
            return ResponseEntity.ok(questionService.getQuestionsByLanguageAndDifficulty(language, difficulty, pageable));
        }
        return ResponseEntity.ok(questionService.getQuestionsByLanguageAndDifficulty(language, difficulty));
    }
}