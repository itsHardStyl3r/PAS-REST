package pl.hardstyl3r.pas.v1.advices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.hardstyl3r.pas.v1.exceptions.AllocationException;

@RestControllerAdvice
public class AllocationExceptionAdvice {
    @ExceptionHandler(AllocationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String allocationHandler(AllocationException ex) {
        return ex.getMessage();
    }
}
