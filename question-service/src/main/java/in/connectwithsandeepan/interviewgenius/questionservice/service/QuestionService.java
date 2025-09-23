package in.connectwithsandeepan.interviewgenius.questionservice.service;

import in.connectwithsandeepan.interviewgenius.questionservice.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {
    Question generateAndSaveQuestionFromAi();
    List<Question> getAllQuestions();
    Page<Question> getAllQuestions(Pageable pageable);
    List<Question> getQuestionsByLanguage(String language);
    Page<Question> getQuestionsByLanguage(String language, Pageable pageable);
    List<Question> getQuestionsByDifficulty(String difficulty);
    Page<Question> getQuestionsByDifficulty(String difficulty, Pageable pageable);
    List<Question> getQuestionsByLanguageAndDifficulty(String language, String difficulty);
    Page<Question> getQuestionsByLanguageAndDifficulty(String language, String difficulty, Pageable pageable);
}