package org.ubb.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ubb.ticketing.domain.TicketElement;
import org.ubb.ticketing.domain.TicketStatus;
import org.ubb.ticketing.domain.TicketType;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreationRequest implements Serializable {

    private TicketType ticketType;
    private TicketElement ticketElement;
    private String description;
    private TicketStatus ticketStatus;

}
