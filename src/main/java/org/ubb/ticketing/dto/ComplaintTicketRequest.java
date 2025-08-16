package org.ubb.ticketing.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintTicketRequest implements Serializable {

    private String ticketElementName;
    private String description;


}
