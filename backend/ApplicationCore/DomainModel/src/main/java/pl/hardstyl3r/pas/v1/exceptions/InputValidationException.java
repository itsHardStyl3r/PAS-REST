package pl.hardstyl3r.pas.v1.exceptions;

public class InputValidationException extends RuntimeException {
    public InputValidationException(String message) {
        super(message);
    }
}

//@RestControllerAdvice
//class InputValidationAdvice {
//    @ExceptionHandler(InputValidationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    String inputValidationHandler(InputValidationException ex) {
//        return ex.getMessage();
//    }
//}
