package pl.hardstyl3r.pas.v1.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.hardstyl3r.pas.v1.exceptions.UserValidationException;

@RestControllerAdvice
public class UserValidationExceptionAdvice {
    @ExceptionHandler(UserValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String userValidationHandler(UserValidationException ex) {
        return ex.getMessage();
    }
}