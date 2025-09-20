package org.ubb.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ubb.ticketing.domain.*;
import org.ubb.ticketing.dto.user.TicketingUserDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDto {

    private Long ticketId;
    private TicketType ticketType;
    private CustomerDto customer;
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
    private List<CommentDto> comments;


}
