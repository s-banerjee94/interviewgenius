package in.connectwithsandeepan.interviewgenius.interviewservice.exception;

import in.connectwithsandeepan.interviewgenius.interviewservice.entity.InterviewSession;

public class InvalidSessionStateException extends RuntimeException {
    public InvalidSessionStateException(String message) {
        super(message);
    }

    public InvalidSessionStateException(String sessionId, InterviewSession.Status currentStatus, String expectedStatus) {
        super(String.format("Session %s is in %s state, expected %s", sessionId, currentStatus, expectedStatus));
    }
}
