package pl.hardstyl3r.userservice.domain.exception;

public class UsernameIsTakenException extends RuntimeException {
    public UsernameIsTakenException(String username) {
        super("Username '" + username + "' is already taken.");
    }
}
