package pl.hardstyl3r.rentservice.domain.exception;

public class ResourceInUseException extends RuntimeException {
    public ResourceInUseException(String message) {
        super(message);
    }
}
