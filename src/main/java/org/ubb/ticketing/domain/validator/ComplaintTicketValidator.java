package org.ubb.ticketing.domain.validator;

import org.springframework.stereotype.Component;
import org.ubb.ticketing.domain.TicketType;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.exception.TicketingSystemException;

@Component
public class ComplaintTicketValidator {

    public boolean validate(ComplaintTicket complaintTicket){
        if (complaintTicket.getTicketType().equals(TicketType.COMPLAINT)) {
            return true;
        }
        throw new TicketingSystemException("Invalid ticket type");
    }


}
