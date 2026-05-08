package pl.hardstyl3r.rentservice.domain.exception;

public class UserValidationException extends RuntimeException {
    public UserValidationException(String message) {
        super(message);
    }
}
