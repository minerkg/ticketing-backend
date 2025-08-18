package org.ubb.ticketing.domain;


import org.springframework.stereotype.Component;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.domain.user.TicketingUser;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class TicketFactory {

    public static Ticket createNewTicket(TicketType type, TicketingUser createdBy) {
        return switch (type) {
            case COMPLAINT -> {
                var ct = new ComplaintTicket();
                ct.setTicketType(TicketType.COMPLAINT);
                ct.setCreatedWhen(LocalDateTime.now());
                ct.setSlaHours(720);
                ct.setTicketStatus(TicketStatus.NEW);
                ct.setCreatedBy(createdBy);
                ct.setComments(new ArrayList<>());
                yield ct;
            }
//                case INCIDENT -> new IncidentTicket();
//                case REQUEST -> new RequestTicket();
            default -> throw new IllegalArgumentException("Unknown ticket type: " + type);
        };
    }
}



