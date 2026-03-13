package pl.hardstyl3r.pas.v1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class UserNotActiveException extends RuntimeException {
    public UserNotActiveException(String message) {
        super(message);
    }
}

@RestControllerAdvice
class UserNotActiveAdvice {

    @ExceptionHandler(UserNotActiveException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String userNotActiveHandler(UserNotActiveException ex) {
        return ex.getMessage();
    }
}