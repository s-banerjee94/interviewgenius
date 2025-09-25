package in.connectwithsandeepan.interviewgenius.useranswerservice.service;

import in.connectwithsandeepan.interviewgenius.useranswerservice.dto.UserAnswerRequest;
import in.connectwithsandeepan.interviewgenius.useranswerservice.dto.UserAnswerResponse;
import in.connectwithsandeepan.interviewgenius.useranswerservice.entity.UserAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserAnswerService {

    UserAnswerResponse submitAnswer(UserAnswerRequest request);

    UserAnswerResponse getUserAnswerById(String id);

    List<UserAnswerResponse> getUserAnswersByUserId(String userId);

    Page<UserAnswerResponse> getUserAnswersByUserId(String userId, Pageable pageable);

    List<UserAnswerResponse> getUserAnswersByQuestionId(String questionId);

    Page<UserAnswerResponse> getUserAnswersByQuestionId(String questionId, Pageable pageable);

    List<UserAnswerResponse> getUserAnswersByUserIdAndQuestionId(String userId, String questionId);

    Page<UserAnswerResponse> getAllUserAnswers(Pageable pageable);

    void deleteUserAnswer(String id);

    long getTotalAnswerCount();

    long getCorrectAnswerCount();

    long getIncorrectAnswerCount();

    double getAccuracyPercentage();

    List<UserAnswerResponse> getRecentAnswers(int limit);
}