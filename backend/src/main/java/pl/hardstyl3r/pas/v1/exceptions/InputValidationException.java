package pl.hardstyl3r.pas.v1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class InputValidationException extends RuntimeException {
    public InputValidationException(String message) {
        super(message);
    }
}

@RestControllerAdvice
class InputValidationAdvice {
    @ExceptionHandler(InputValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String inputValidationHandler(InputValidationException ex) {
        return ex.getMessage();
    }
}
