package org.ubb.ticketing.domain.complaint;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.ubb.ticketing.domain.Ticket;
import org.ubb.ticketing.domain.TicketType;
import org.ubb.ticketing.domain.user.TicketingUser;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
public class ComplaintTicket extends Ticket<Complaint> {


    @Enumerated(EnumType.STRING)
    private ComplaintType complaintType;

    public ComplaintTicket() {
        super.setTicketType(TicketType.COMPLAINT);
        super.setCreatedWhen(LocalDateTime.now());
    }





}
