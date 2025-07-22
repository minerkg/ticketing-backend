package org.ubb.ticketing.domain.complaint;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.ubb.ticketing.domain.Ticket;
import org.ubb.ticketing.domain.TicketType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
public class ComplaintTicket extends Ticket {


    public ComplaintTicket() {
        super.setTicketType(TicketType.COMPLAINT);
        super.setCreatedWhen(LocalDateTime.now());
        super.setSlaHours(720);

    }


}
