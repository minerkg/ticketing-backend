package org.ubb.ticketing.domain.complaint;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.ubb.ticketing.domain.Ticket;
import org.ubb.ticketing.domain.TicketStatus;
import org.ubb.ticketing.domain.TicketType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
public class ComplaintTicket extends Ticket {


    protected ComplaintTicket(ComplaintTicketBuilder<?, ?> builder) {
        super(builder);
    }


    public static class ComplaintTicketBuilderImpl extends ComplaintTicketBuilder<ComplaintTicket, ComplaintTicketBuilderImpl> {
        @Override
        public ComplaintTicket build() {
            var ct = new ComplaintTicket(this);
            ct.setTicketType(TicketType.COMPLAINT);
            ct.setCreatedWhen(LocalDateTime.now());
            ct.setSlaHours(720);
            ct.setTicketStatus(TicketStatus.NEW);
            return ct;
        }
    }

    public static ComplaintTicketBuilder<?, ?> builder() {
        return new ComplaintTicketBuilderImpl();
    }


}
