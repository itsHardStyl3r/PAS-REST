package pl.hardstyl3r.pas.v1.exceptions;


public class UserNotActiveException extends RuntimeException {
    public UserNotActiveException(String message) {
        super(message);
    }
}
