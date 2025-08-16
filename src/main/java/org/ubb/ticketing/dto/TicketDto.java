package org.ubb.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ubb.ticketing.domain.SolutionType;
import org.ubb.ticketing.domain.TicketElement;
import org.ubb.ticketing.domain.TicketStatus;
import org.ubb.ticketing.domain.TicketType;
import org.ubb.ticketing.domain.user.TicketingUser;

import java.time.LocalDateTime;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDto {

    private Long ticketId;
    private TicketType ticketType;
    private TicketElement ticketElement;
    private LocalDateTime createdWhen;
    private TicketingUserDto createdBy;
    private String description;
    private TicketStatus ticketStatus;
    private Integer slaHours;
    private TicketingUserDto assignedTo;
    private LocalDateTime assignedWhen;
    private SolutionType solutionType;
    private String solutionDescription;
    private TicketingUserDto closedBy;
    private LocalDateTime closedWhen;
    private TicketingUserDto cancelledBy;
    private LocalDateTime cancelledWhen;


}
