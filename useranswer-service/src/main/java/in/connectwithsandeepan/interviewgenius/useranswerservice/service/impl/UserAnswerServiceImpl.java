package in.connectwithsandeepan.interviewgenius.useranswerservice.service.impl;

import in.connectwithsandeepan.interviewgenius.useranswerservice.client.QuestionServiceClient;
import in.connectwithsandeepan.interviewgenius.useranswerservice.client.UserServiceClient;
import in.connectwithsandeepan.interviewgenius.useranswerservice.dto.Question;
import in.connectwithsandeepan.interviewgenius.useranswerservice.dto.UserAnswerRequest;
import in.connectwithsandeepan.interviewgenius.useranswerservice.dto.UserAnswerResponse;
import in.connectwithsandeepan.interviewgenius.useranswerservice.dto.UserResponse;
import in.connectwithsandeepan.interviewgenius.useranswerservice.entity.UserAnswer;
import in.connectwithsandeepan.interviewgenius.useranswerservice.repository.UserAnswerRepository;
import in.connectwithsandeepan.interviewgenius.useranswerservice.service.UserAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAnswerServiceImpl implements UserAnswerService {

    private final UserAnswerRepository userAnswerRepository;
    private final UserServiceClient userServiceClient;
    private final QuestionServiceClient questionServiceClient;

    @Override
    public UserAnswerResponse submitAnswer(UserAnswerRequest request) {
        try {
            userServiceClient.getUserById(Long.valueOf(request.getUserId()));
        } catch (Exception e) {
            throw new RuntimeException("User not found with id: " + request.getUserId());
        }

        try {
            questionServiceClient.getQuestionById(request.getQuestionId());
        } catch (Exception e) {
            throw new RuntimeException("Question not found with id: " + request.getQuestionId());
        }

        UserAnswer userAnswer = UserAnswer.builder()
                .userId(request.getUserId())
                .questionId(request.getQuestionId())
                .givenAnswer(request.getGivenAnswer())
                .correct(request.getCorrect())
                .submittedAt(LocalDateTime.now())
                .build();

        UserAnswer savedAnswer = userAnswerRepository.save(userAnswer);
        return UserAnswerResponse.fromUserAnswer(savedAnswer);
    }

    @Override
    public UserAnswerResponse getUserAnswerById(String id) {
        UserAnswer userAnswer = userAnswerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserAnswer not found with id: " + id));
        return UserAnswerResponse.fromUserAnswer(userAnswer);
    }

    @Override
    public List<UserAnswerResponse> getUserAnswersByUserId(String userId) {
        List<UserAnswer> userAnswers = userAnswerRepository.findByUserId(userId);
        return userAnswers.stream()
                .map(UserAnswerResponse::fromUserAnswer)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserAnswerResponse> getUserAnswersByUserId(String userId, Pageable pageable) {
        Page<UserAnswer> userAnswers = userAnswerRepository.findByUserId(userId, pageable);
        return userAnswers.map(UserAnswerResponse::fromUserAnswer);
    }

    @Override
    public List<UserAnswerResponse> getUserAnswersByQuestionId(String questionId) {
        List<UserAnswer> userAnswers = userAnswerRepository.findByQuestionId(questionId);
        return userAnswers.stream()
                .map(UserAnswerResponse::fromUserAnswer)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserAnswerResponse> getUserAnswersByQuestionId(String questionId, Pageable pageable) {
        Page<UserAnswer> userAnswers = userAnswerRepository.findByQuestionId(questionId, pageable);
        return userAnswers.map(UserAnswerResponse::fromUserAnswer);
    }

    @Override
    public List<UserAnswerResponse> getUserAnswersByUserIdAndQuestionId(String userId, String questionId) {
        List<UserAnswer> userAnswers = userAnswerRepository.findByUserIdAndQuestionId(userId, questionId);
        return userAnswers.stream()
                .map(UserAnswerResponse::fromUserAnswer)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserAnswerResponse> getAllUserAnswers(Pageable pageable) {
        Page<UserAnswer> userAnswers = userAnswerRepository.findAll(pageable);
        return userAnswers.map(UserAnswerResponse::fromUserAnswer);
    }

    @Override
    public void deleteUserAnswer(String id) {
        userAnswerRepository.deleteById(id);
    }

    @Override
    public long getTotalAnswerCount() {
        return userAnswerRepository.count();
    }

    @Override
    public long getCorrectAnswerCount() {
        return userAnswerRepository.countByCorrect(true);
    }

    @Override
    public long getIncorrectAnswerCount() {
        return userAnswerRepository.countByCorrect(false);
    }

    @Override
    public double getAccuracyPercentage() {
        long total = getTotalAnswerCount();
        if (total == 0) return 0.0;
        long correct = getCorrectAnswerCount();
        return (double) correct / total * 100;
    }

    @Override
    public List<UserAnswerResponse> getRecentAnswers(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "submittedAt"));
        Page<UserAnswer> recentAnswers = userAnswerRepository.findAll(pageable);
        return recentAnswers.stream()
                .map(UserAnswerResponse::fromUserAnswer)
                .collect(Collectors.toList());
    }
}
