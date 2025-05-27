package whz.project.demo.excpetions;

public class NotFoundByLoginException extends RuntimeException{
    public NotFoundByLoginException(String message) {
        super(message);
    }
}
