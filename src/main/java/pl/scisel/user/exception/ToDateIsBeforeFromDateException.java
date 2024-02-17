package pl.scisel.user.exception;

public class ToDateIsBeforeFromDateException extends RuntimeException {
    public ToDateIsBeforeFromDateException(String message) {
        super(message);
    }
}
