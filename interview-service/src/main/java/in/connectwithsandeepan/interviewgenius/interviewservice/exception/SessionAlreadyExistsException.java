package in.connectwithsandeepan.interviewgenius.interviewservice.exception;

public class SessionAlreadyExistsException extends RuntimeException {
    public SessionAlreadyExistsException(String userId) {
        super(String.format("User %s already has an active interview session", userId));
    }
}
