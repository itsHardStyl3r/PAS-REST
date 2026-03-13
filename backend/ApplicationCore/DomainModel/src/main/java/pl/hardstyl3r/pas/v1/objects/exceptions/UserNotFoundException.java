package pl.hardstyl3r.pas.v1.objects.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

//@RestControllerAdvice
//class UserNotFoundAdvice {
//
//    @ExceptionHandler(UserNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    String userNotFoundHandler(UserNotFoundException ex) {
//        return ex.getMessage();
//    }
//}