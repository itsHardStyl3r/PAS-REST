package pl.hardstyl3r.rentservice.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.hardstyl3r.pas.v1.exceptions.ResourceNotFoundException;

@RestControllerAdvice
public class ResourceNotFoundExceptionAdvice {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<String> resourceNotFoundHandler(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
