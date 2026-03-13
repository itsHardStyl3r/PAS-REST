package pl.hardstyl3r.pas.v1.objects.exceptions;

public class AllocationException extends RuntimeException {
    public AllocationException(String message) {
        super(message);
    }
}

//@RestControllerAdvice
//class AllocationAdvice {
//    @ExceptionHandler(AllocationException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    String allocationHandler(AllocationException ex) {
//        return ex.getMessage();
//    }
//}
