package in.connectwithsandeepan.interviewgenius.userservice.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException() {
        super("Invalid password");
    }
}
