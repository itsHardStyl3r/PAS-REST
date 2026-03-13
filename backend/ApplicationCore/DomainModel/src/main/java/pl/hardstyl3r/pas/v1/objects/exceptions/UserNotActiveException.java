package pl.hardstyl3r.pas.v1.objects.exceptions;


public class UserNotActiveException extends RuntimeException {
    public UserNotActiveException(String message) {
        super(message);
    }
}

//@RestControllerAdvice
//class UserNotActiveAdvice {
//
//    @ExceptionHandler(UserNotActiveException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    String userNotActiveHandler(UserNotActiveException ex) {
//        return ex.getMessage();
//    }
//}