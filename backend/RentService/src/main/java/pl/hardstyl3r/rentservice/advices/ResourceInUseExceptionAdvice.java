package pl.hardstyl3r.rentservice.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.hardstyl3r.pas.v1.exceptions.ResourceInUseException;

@RestControllerAdvice
public class ResourceInUseExceptionAdvice {
    @ExceptionHandler(ResourceInUseException.class)
    ResponseEntity<String> resourceInUseHandler(ResourceInUseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
