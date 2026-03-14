package pl.hardstyl3r.pas.v1.exceptions;


public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

//@RestControllerAdvice
//class ResourceNotFoundAdvice {
//    @ExceptionHandler(ResourceNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    String resourceNotFoundHandler(ResourceNotFoundException ex) {
//        return ex.getMessage();
//    }
//}
