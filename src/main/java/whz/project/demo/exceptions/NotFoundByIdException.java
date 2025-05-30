package whz.project.demo.exceptions;

public class NotFoundByIdException extends RuntimeException{
    public NotFoundByIdException(String message) {
        super(message);
    }
}
