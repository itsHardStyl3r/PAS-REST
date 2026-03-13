package pl.hardstyl3r.pas.v1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class UsernameIsTakenException extends RuntimeException {
    public UsernameIsTakenException(String username) {
        super("Username '" + username + "' is already taken.");
    }
}

@RestControllerAdvice
class UsernameIsTakenAdvice {
    @ExceptionHandler(UsernameIsTakenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String usernameIsTakenHandler(UsernameIsTakenException ex) {
        return ex.getMessage();
    }
}
