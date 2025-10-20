package in.connectwithsandeepan.interviewgenius.interviewservice.service;

import in.connectwithsandeepan.interviewgenius.interviewservice.client.AiClient;
import in.connectwithsandeepan.interviewgenius.interviewservice.dto.*;
import in.connectwithsandeepan.interviewgenius.interviewservice.entity.InterviewSession;
import in.connectwithsandeepan.interviewgenius.interviewservice.entity.QuestionAnswer;
import in.connectwithsandeepan.interviewgenius.interviewservice.exception.*;
import in.connectwithsandeepan.interviewgenius.interviewservice.repository.InterviewSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewSessionRepository repository;
    private final AiClient aiClient;

    public InterviewSession startSession(String userId, String experienceLevel, String language) {
        InterviewSession existingSession = repository.findByUserIdAndStatus(userId, InterviewSession.Status.ACTIVE);
        if (existingSession != null) {
            throw new SessionAlreadyExistsException(userId);
        }

        InterviewSession session = new InterviewSession();
        session.setUserId(userId);
        session.setStartTime(LocalDateTime.now());
        session.setStatus(InterviewSession.Status.ACTIVE);
        session.setQuestionAnswers(new ArrayList<>());
        session.setExperienceLevel(experienceLevel);
        session.setLanguage(language);
        return repository.save(session);
    }


    public QuestionDto getFirstQuestion(String sessionId) {
        InterviewSession session = repository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId, "Cannot get first question"));

        if (session.getStatus() != InterviewSession.Status.ACTIVE) {
            throw new InvalidSessionStateException(sessionId, session.getStatus(), "ACTIVE");
        }

        // Get the first question from AI interview chat client
        InterviewStartResponseDto response = aiClient.startInterview(
            sessionId,
            session.getExperienceLevel(),
            session.getLanguage()
        );
        String questionText = response.getQuestion();

        // Create new QuestionAnswer for the first question
        QuestionAnswer qa = new QuestionAnswer();
        qa.setQuestionIndex(1);
        qa.setQuestion(questionText);
        qa.setQuestionTimestamp(LocalDateTime.now());
        session.getQuestionAnswers().add(qa);
        repository.save(session);

        return QuestionDto.builder().question(questionText).build();
    }

    public AnswerSubmissionResponseDto submitAnswer(String sessionId, String audioFilePath) {
        InterviewSession session = repository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId, "Cannot submit answer"));

        if (session.getStatus() != InterviewSession.Status.ACTIVE) {
            throw new InvalidSessionStateException(sessionId, session.getStatus(), "ACTIVE");
        }

        // Transcribe audio using Whisper AI
        String transcription = aiClient.transcribeAudio(audioFilePath);

        // Validate: Ensure there's a question to answer
        List<QuestionAnswer> qaList = session.getQuestionAnswers();
        if (qaList.isEmpty()) {
            throw new InvalidSessionStateException("No question found to answer. Get a question first.");
        }

        // Get current (last) question and validate it hasn't been answered
        QuestionAnswer lastQa = qaList.get(qaList.size() - 1);
        if (lastQa.getAnswer() != null) {
            throw new AnswerAlreadySubmittedException(sessionId);
        }

        // Update the current question with answer details
        lastQa.setAnswer(transcription);
        lastQa.setAudioFileUrl(audioFilePath);
        lastQa.setAnswerTimestamp(LocalDateTime.now());

        // Get feedback and next question from interview chat client
        InterviewResponseDto interviewResponse = aiClient.submitAnswer(sessionId, transcription);

        // Save feedback and score for the answered question
        if (interviewResponse.getFeedback() != null) {
            lastQa.setFeedback(interviewResponse.getFeedback().getFeedback());
            lastQa.setScore(interviewResponse.getFeedback().getScore());
        }

        // Create QuestionAnswer entry for the next question from AI
        String nextQuestionText = interviewResponse.getQuestion();
        if (nextQuestionText != null && !nextQuestionText.isEmpty()) {
            int nextIndex = qaList.size() + 1;
            QuestionAnswer nextQa = new QuestionAnswer();
            nextQa.setQuestionIndex(nextIndex);
            nextQa.setQuestion(nextQuestionText);
            nextQa.setQuestionTimestamp(LocalDateTime.now());
            session.getQuestionAnswers().add(nextQa);
        }

        repository.save(session);

        // Count total answered questions
        long totalAnswered = qaList.stream()
                .filter(qa -> qa.getAnswer() != null)
                .count();

        // Build and return response
        return AnswerSubmissionResponseDto.builder()
                .questionIndex(lastQa.getQuestionIndex())
                .answer(transcription)
                .question(lastQa.getQuestion())
                .totalQuestionsAnswered((int) totalAnswered)
                .sessionStatus(session.getStatus().name())
                .feedback(interviewResponse.getFeedback())
                .nextQuestion(nextQuestionText)
                .build();
    }

    public SessionDetailsDto getSessionDetails(String sessionId) {
        InterviewSession session = repository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId, "Cannot retrieve session details"));

        List<QuestionAnswerDto> qaList = session.getQuestionAnswers().stream()
                .map(qa -> new QuestionAnswerDto(
                        qa.getQuestionIndex(),
                        qa.getQuestion(),
                        qa.getAnswer(),
                        qa.getAudioFileUrl(),
                        qa.getQuestionTimestamp(),
                        qa.getAnswerTimestamp(),
                        qa.getFeedback(),
                        qa.getScore()
                ))
                .collect(Collectors.toList());

        return new SessionDetailsDto(
                session.getId(),  // Maps to sessionId field in DTO
                session.getUserId(),
                session.getStartTime(),
                session.getEndTime(),
                session.getStatus(),
                qaList
        );
    }

    public InterviewSession endSession(String sessionId, boolean force) {
        InterviewSession session = repository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(sessionId, "Cannot end session"));

        if (session.getStatus() == InterviewSession.Status.COMPLETED) {
            return session;
        }

        if (force) {
            session.setEndTime(LocalDateTime.now());
            session.setStatus(InterviewSession.Status.COMPLETED);
            return repository.save(session);
        } else {
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime expectedEndTime = session.getStartTime().plusMinutes(30).plusSeconds(30);

            if (currentTime.isAfter(expectedEndTime) || currentTime.isEqual(expectedEndTime)) {
                session.setEndTime(currentTime);
                session.setStatus(InterviewSession.Status.COMPLETED);
                return repository.save(session);
            } else {
                long remainingMinutes = Duration.between(currentTime, expectedEndTime).toMinutes();
                throw new SessionTimeNotCompletedException(remainingMinutes);
            }
        }
    }

    public List<SessionListDto> getAllSessions(String userId, boolean pagination, int page, int size) {
        List<InterviewSession> sessions;

        if (pagination) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
            Page<InterviewSession> sessionPage = repository.findByUserId(userId, pageable);
            sessions = sessionPage.getContent();
        } else {
            sessions = repository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "startTime"));
        }

        return sessions.stream()
                .map(session -> SessionListDto.builder()
                        .sessionId(session.getId())
                        .language(session.getLanguage())
                        .startTime(session.getStartTime())
                        .experienceLevel(session.getExperienceLevel())
                        .build())
                .collect(Collectors.toList());
    }
}
