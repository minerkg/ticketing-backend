package org.ubb.ticketing.domain.complaint;


import jakarta.persistence.Entity;
import lombok.*;
import org.ubb.ticketing.domain.Ticket;
import org.ubb.ticketing.domain.TicketType;
import org.ubb.ticketing.domain.complaint.ComplaintType;
import org.ubb.ticketing.domain.user.TicketingUser;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
public class ComplaintTicket extends Ticket {


    private TicketingUser createdBy;
    private ComplaintType complaintType;

    public ComplaintTicket() {
        super.setTicketType(TicketType.COMPLAINT);
        super.setCreatedWhen(LocalDateTime.now());
    }


    private ComplaintSolutionType complaintSolutionType;
    private String solutionDescription;
    private TicketingUser closedBy;
    private LocalDateTime closedWhen;




}
