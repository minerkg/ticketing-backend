package org.ubb.ticketing.Exceptions;

public class TicketingSystemException extends RuntimeException {

    public TicketingSystemException(String message) {
        super(message);
    }
    public TicketingSystemException(String message, Throwable cause) {
        super(message, cause);
    }
    public TicketingSystemException(Throwable cause) {
        super(cause);
    }
}
