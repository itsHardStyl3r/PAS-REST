package pl.hardstyl3r.pas.v1.exceptions;


public class ResourceValidationException extends RuntimeException {
    public ResourceValidationException(String message) {
        super(message);
    }
}

//@RestControllerAdvice
//class ResourceValidationAdvice {
//    @ExceptionHandler(ResourceValidationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    String resourceValidationHandler(ResourceValidationException ex) {
//        return ex.getMessage();
//    }
//}
