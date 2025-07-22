package org.ubb.ticketing.domain;


import org.springframework.stereotype.Component;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;

@Component
public class TicketFactory {

    public Ticket createTicket(TicketType type) {
        return switch (type) {
            case COMPLAINT -> new ComplaintTicket();
//                case INCIDENT -> new IncidentTicket();
//                case REQUEST -> new RequestTicket();
            default -> throw new IllegalArgumentException("Unknown ticket type: " + type);
        };
    }
}



