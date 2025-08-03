package org.ubb.ticketing.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ubb.ticketing.domain.TicketElement;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintTicketRequest implements Serializable {

    private TicketElement ticketElement;
    private String description;


}
