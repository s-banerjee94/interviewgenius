package in.connectwithsandeepan.interviewgenius.userservice.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long userId) {
        super(String.format("User not found with ID: %d", userId));
    }

    public UserNotFoundException(String field, String value) {
        super(String.format("User not found with %s: %s", field, value));
    }
}
