package in.connectwithsandeepan.interviewgenius.interviewservice.exception;

public class SessionTimeNotCompletedException extends RuntimeException {
    private final long remainingMinutes;

    public SessionTimeNotCompletedException(long remainingMinutes) {
        super(String.format("Interview session time not completed yet. Remaining time: %d minutes", remainingMinutes));
        this.remainingMinutes = remainingMinutes;
    }

    public long getRemainingMinutes() {
        return remainingMinutes;
    }
}
