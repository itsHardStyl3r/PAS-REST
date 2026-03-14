package pl.hardstyl3r.pas.v1.exceptions;

public class ResourceInUseException extends RuntimeException {
    public ResourceInUseException(String message) {
        super(message);
    }
}

//@RestControllerAdvice
//class ResourceInUseAdvice {
//    @ExceptionHandler(ResourceInUseException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    String resourceInUseHandler(ResourceInUseException ex) {
//        return ex.getMessage();
//    }
//}
