package org.ubb.ticketing.domain.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.ubb.ticketing.domain.TicketType;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;

@Component
public class ComplaintTicketValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return ComplaintTicket.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ComplaintTicket complaintTicket = (ComplaintTicket) target;

        if (!complaintTicket.getTicketType().equals(TicketType.COMPLAINT)) {
            errors.rejectValue("ticketType", "mismatch",
                    "The complaint ticket must have ticket type COMPLAINT");
        }

    }
}
