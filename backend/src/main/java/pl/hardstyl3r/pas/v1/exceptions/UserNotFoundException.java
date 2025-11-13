package pl.hardstyl3r.pas.v1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Integer id) {
        super("User #" + id + " not found.");
    }

    public UserNotFoundException(String name) {
        super("User '" + name + "' not found.");
    }
}

@RestControllerAdvice
class UserNotFoundAdvice {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String userNotFoundHandler(UserNotFoundException ex) {
        return ex.getMessage();
    }
}