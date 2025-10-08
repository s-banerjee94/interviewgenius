package in.connectwithsandeepan.interviewgenius.interviewservice.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(String message) {
        super(message);
    }

    public SessionNotFoundException(String sessionId, String reason) {
        super(String.format("Session not found with id: %s. %s", sessionId, reason));
    }
}
