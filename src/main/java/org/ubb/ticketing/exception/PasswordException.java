package org.ubb.ticketing.exception;

public class PasswordException extends TicketingSystemException {

    public PasswordException(String message) {
        super(message);
    }

    public PasswordException(Throwable cause) {
        super(cause);
    }

    public PasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
