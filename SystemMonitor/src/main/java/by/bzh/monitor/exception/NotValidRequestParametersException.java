package by.bzh.monitor.exception;

public class NotValidRequestParametersException extends RuntimeException{
    public NotValidRequestParametersException() {
    }

    public NotValidRequestParametersException(String message) {
        super(message);
    }

    public NotValidRequestParametersException(String message, Throwable cause) {
        super(message, cause);
    }
}
