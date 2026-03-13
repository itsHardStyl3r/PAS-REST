package pl.hardstyl3r.pas.v1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class UserValidationException extends RuntimeException {
    public UserValidationException(String message) {
        super(message);
    }
}

@RestControllerAdvice
class UserValidationAdvice {
    @ExceptionHandler(UserValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String userValidationHandler(UserValidationException ex) {
        return ex.getMessage();
    }
}