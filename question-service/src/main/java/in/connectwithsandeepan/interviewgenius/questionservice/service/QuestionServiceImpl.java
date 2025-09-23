package in.connectwithsandeepan.interviewgenius.questionservice.service;

import in.connectwithsandeepan.interviewgenius.questionservice.client.AiServiceClient;
import in.connectwithsandeepan.interviewgenius.questionservice.entity.Question;
import in.connectwithsandeepan.interviewgenius.questionservice.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final AiServiceClient aiServiceClient;
    private final QuestionRepository questionRepository;

    @Override
    public Question generateAndSaveQuestionFromAi() {
        Question question = aiServiceClient.getQuestion();
        return questionRepository.save(question);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public Page<Question> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    @Override
    public List<Question> getQuestionsByLanguage(String language) {
        return questionRepository.findByProgramingLanguage(language);
    }

    @Override
    public Page<Question> getQuestionsByLanguage(String language, Pageable pageable) {
        return questionRepository.findByProgramingLanguage(language, pageable);
    }

    @Override
    public List<Question> getQuestionsByDifficulty(String difficulty) {
        return questionRepository.findByDifficulty(difficulty);
    }

    @Override
    public Page<Question> getQuestionsByDifficulty(String difficulty, Pageable pageable) {
        return questionRepository.findByDifficulty(difficulty, pageable);
    }

    @Override
    public List<Question> getQuestionsByLanguageAndDifficulty(String language, String difficulty) {
        return questionRepository.findByProgramingLanguageAndDifficulty(language, difficulty);
    }

    @Override
    public Page<Question> getQuestionsByLanguageAndDifficulty(String language, String difficulty, Pageable pageable) {
        return questionRepository.findByProgramingLanguageAndDifficulty(language, difficulty, pageable);
    }
}