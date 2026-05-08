package pl.hardstyl3r.userservice.domain.exception;

public class UserValidationException extends RuntimeException {
    public UserValidationException(String message) {
        super(message);
    }
}
