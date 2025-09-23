package in.connectwithsandeepan.interviewgenius.questionservice.repository;

import in.connectwithsandeepan.interviewgenius.questionservice.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {

    List<Question> findByProgramingLanguage(String programingLanguage);
    Page<Question> findByProgramingLanguage(String programingLanguage, Pageable pageable);

    List<Question> findByDifficulty(String difficulty);
    Page<Question> findByDifficulty(String difficulty, Pageable pageable);

    List<Question> findByProgramingLanguageAndDifficulty(String programingLanguage, String difficulty);
    Page<Question> findByProgramingLanguageAndDifficulty(String programingLanguage, String difficulty, Pageable pageable);
}