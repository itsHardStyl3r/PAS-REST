package pl.hardstyl3r.rentservice.domain.exception;

public class ResourceValidationException extends RuntimeException {
    public ResourceValidationException(String message) {
        super(message);
    }
}
