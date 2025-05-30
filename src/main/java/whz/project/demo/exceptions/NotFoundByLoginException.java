package whz.project.demo.exceptions;

public class NotFoundByLoginException extends RuntimeException{
    public NotFoundByLoginException(String message) {
        super(message);
    }
}
