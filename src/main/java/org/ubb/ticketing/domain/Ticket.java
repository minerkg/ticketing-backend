package org.ubb.ticketing.domain;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.ubb.ticketing.domain.user.TicketingUser;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    private TicketType ticketType;

    private LocalDateTime createdWhen;

    private String description;

}
