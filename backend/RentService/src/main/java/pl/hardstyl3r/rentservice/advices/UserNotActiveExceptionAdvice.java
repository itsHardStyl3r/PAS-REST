package pl.hardstyl3r.rentservice.advices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.hardstyl3r.pas.v1.exceptions.UserNotActiveException;

@RestControllerAdvice
public class UserNotActiveExceptionAdvice {

    @ExceptionHandler(UserNotActiveException.class)
    ResponseEntity<String> userNotActiveHandler(UserNotActiveException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
