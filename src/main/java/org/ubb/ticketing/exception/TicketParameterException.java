package org.ubb.ticketing.exception;

public class TicketParameterException extends TicketingSystemException {

    public TicketParameterException(String message) {
        super(message);
    }

    public TicketParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public TicketParameterException(Throwable cause) {
        super(cause);
    }
}
