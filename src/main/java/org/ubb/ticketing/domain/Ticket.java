package org.ubb.ticketing.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.ubb.ticketing.domain.user.TicketingUser;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class Ticket<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;


    @Enumerated
    private TicketType ticketType;

    private LocalDateTime createdWhen;

    @ManyToOne
    private TicketingUser createdBy;

    private String description;

    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;



    private SolutionTypes solutionTypes;
    private String solutionDescription;
    @ManyToOne
    private TicketingUser closedBy;
    private LocalDateTime closedWhen;


}
