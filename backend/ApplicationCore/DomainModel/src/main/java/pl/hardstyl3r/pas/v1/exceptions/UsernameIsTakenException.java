package pl.hardstyl3r.pas.v1.exceptions;

public class UsernameIsTakenException extends RuntimeException {
    public UsernameIsTakenException(String username) {
        super("Username '" + username + "' is already taken.");
    }
}
