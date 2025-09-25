package in.connectwithsandeepan.interviewgenius.useranswerservice.client;

import in.connectwithsandeepan.interviewgenius.useranswerservice.dto.Question;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "question-service", path = "/api/v1/questions")
public interface QuestionServiceClient {

    @GetMapping("/{id}")
    Question getQuestionById(@PathVariable String id);

    @GetMapping
    List<Question> getAllQuestions(@RequestParam(defaultValue = "false") boolean paginated);

    @GetMapping
    Page<Question> getAllQuestions(@RequestParam(defaultValue = "true") boolean paginated,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size);

    @GetMapping("/by-language")
    List<Question> getQuestionsByLanguage(@RequestParam String language,
                                         @RequestParam(defaultValue = "false") boolean paginated);

    @GetMapping("/by-difficulty")
    List<Question> getQuestionsByDifficulty(@RequestParam String difficulty,
                                           @RequestParam(defaultValue = "false") boolean paginated);

    @GetMapping("/filter")
    List<Question> getQuestionsByLanguageAndDifficulty(@RequestParam String language,
                                                      @RequestParam String difficulty,
                                                      @RequestParam(defaultValue = "false") boolean paginated);
}