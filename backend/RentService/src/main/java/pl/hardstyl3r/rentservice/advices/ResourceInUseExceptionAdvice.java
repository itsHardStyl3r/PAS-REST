package pl.hardstyl3r.rentservice.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.hardstyl3r.rentservice.domain.exception.ResourceInUseException;

@RestControllerAdvice
public class ResourceInUseExceptionAdvice {
    @ExceptionHandler(ResourceInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String resourceInUseHandler(ResourceInUseException ex) {
        return ex.getMessage();
    }
}
