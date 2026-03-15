package pl.hardstyl3r.pas.v1.exceptions;

public class ResourceInUseException extends RuntimeException {
    public ResourceInUseException(String message) {
        super(message);
    }
}
