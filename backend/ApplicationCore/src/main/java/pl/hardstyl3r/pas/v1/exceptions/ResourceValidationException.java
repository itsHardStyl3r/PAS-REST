package pl.hardstyl3r.pas.v1.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class ResourceValidationException extends RuntimeException {
    public ResourceValidationException(String message) {
        super(message);
    }
}

@RestControllerAdvice
class ResourceValidationAdvice {
    @ExceptionHandler(ResourceValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String resourceValidationHandler(ResourceValidationException ex) {
        return ex.getMessage();
    }
}
