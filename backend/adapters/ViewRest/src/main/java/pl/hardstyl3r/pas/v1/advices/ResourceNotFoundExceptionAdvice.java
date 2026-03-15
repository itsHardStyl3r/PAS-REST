package pl.hardstyl3r.pas.v1.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.hardstyl3r.pas.v1.exceptions.ResourceNotFoundException;

@RestControllerAdvice
public class ResourceNotFoundExceptionAdvice {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String resourceNotFoundHandler(ResourceNotFoundException ex) {
        return ex.getMessage();
    }
}
