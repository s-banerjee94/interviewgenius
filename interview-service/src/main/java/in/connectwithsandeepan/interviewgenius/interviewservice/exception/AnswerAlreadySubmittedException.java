package in.connectwithsandeepan.interviewgenius.interviewservice.exception;

public class AnswerAlreadySubmittedException extends RuntimeException {
    public AnswerAlreadySubmittedException(String sessionId) {
        super(String.format("Answer already submitted for current question in session %s", sessionId));
    }
}
