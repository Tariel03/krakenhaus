package whz.project.demo.exceptions;

public class CannotUploadImageException extends RuntimeException{
    public CannotUploadImageException(String message) {
        super(message);
    }
}
