package pl.hardstyl3r.rentservice.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.hardstyl3r.pas.v1.exceptions.ResourceValidationException;

@RestControllerAdvice
public class ResourceValidationExceptionAdvice {
    @ExceptionHandler(ResourceValidationException.class)
    ResponseEntity<String> resourceValidationHandler(ResourceValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
