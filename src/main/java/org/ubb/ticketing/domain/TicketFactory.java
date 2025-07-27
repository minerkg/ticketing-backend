package org.ubb.ticketing.domain;


import org.springframework.stereotype.Component;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;

@Component
public class TicketFactory {

    public static Ticket createTicket(TicketType type) {
        return switch (type) {
            case COMPLAINT -> ComplaintTicket.builder().build();
//                case INCIDENT -> new IncidentTicket();
//                case REQUEST -> new RequestTicket();
            default -> throw new IllegalArgumentException("Unknown ticket type: " + type);
        };
    }
}



