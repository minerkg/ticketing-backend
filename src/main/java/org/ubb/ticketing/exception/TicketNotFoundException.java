package org.ubb.ticketing.exception;

public class TicketNotFoundException extends TicketingSystemException{
    public TicketNotFoundException(String message) {
        super(message);
    }
    public TicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public TicketNotFoundException(Throwable cause) {
        super(cause);
    }

}
