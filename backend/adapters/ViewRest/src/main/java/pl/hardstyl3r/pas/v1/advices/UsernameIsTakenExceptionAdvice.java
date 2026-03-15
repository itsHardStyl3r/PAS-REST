package pl.hardstyl3r.pas.v1.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.hardstyl3r.pas.v1.exceptions.UsernameIsTakenException;

@RestControllerAdvice
public class UsernameIsTakenExceptionAdvice {
    @ExceptionHandler(UsernameIsTakenException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String usernameIsTakenHandler(UsernameIsTakenException ex) {
        return ex.getMessage();
    }
}
