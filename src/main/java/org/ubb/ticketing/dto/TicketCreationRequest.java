package org.ubb.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ubb.ticketing.domain.TicketStatus;
import org.ubb.ticketing.domain.TicketType;
import org.ubb.ticketing.domain.customer.Customer;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreationRequest implements Serializable {


    private UUID customerId;
    private TicketType ticketType;
    private String ticketElementName;
    private String description;
    private TicketStatus ticketStatus;

}
