package org.ubb.ticketing.dto;

import lombok.Builder;
import lombok.Data;
import org.ubb.ticketing.domain.SolutionType;
import org.ubb.ticketing.domain.TicketElement;
import org.ubb.ticketing.domain.TicketStatus;
import org.ubb.ticketing.domain.TicketType;
import org.ubb.ticketing.domain.user.TicketingUser;

import java.time.LocalDateTime;
@Builder
@Data
public class TicketDto {

    private Long ticketId;
    private TicketType ticketType;
    private TicketElement ticketElement;
    private LocalDateTime createdWhen;
    private TicketingUser createdBy;
    private String description;
    private TicketStatus ticketStatus;
    private Integer slaHours;
    private TicketingUser assignedTo;
    private LocalDateTime assignedWhen;
    private SolutionType solutionType;
    private String solutionDescription;
    private TicketingUser closedBy;
    private LocalDateTime closedWhen;


}
