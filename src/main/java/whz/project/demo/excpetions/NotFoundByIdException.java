package whz.project.demo.excpetions;

public class NotFoundByIdException extends RuntimeException{
    public NotFoundByIdException(String message) {
        super(message);
    }
}
