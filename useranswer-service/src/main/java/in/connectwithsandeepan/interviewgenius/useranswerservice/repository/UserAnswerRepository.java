package in.connectwithsandeepan.interviewgenius.useranswerservice.repository;

import in.connectwithsandeepan.interviewgenius.useranswerservice.entity.UserAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserAnswerRepository extends MongoRepository<UserAnswer, String> {

    List<UserAnswer> findByUserId(String userId);

    Page<UserAnswer> findByUserId(String userId, Pageable pageable);

    List<UserAnswer> findByQuestionId(String questionId);

    Page<UserAnswer> findByQuestionId(String questionId, Pageable pageable);

    List<UserAnswer> findByUserIdAndQuestionId(String userId, String questionId);

    long countByCorrect(Boolean correct);
}
