package org.ubb.ticketing.domain;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.ubb.ticketing.domain.client.Client;
import org.ubb.ticketing.domain.user.TicketingUser;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@MappedSuperclass
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@SuperBuilder
public abstract class Ticket implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @ManyToOne(cascade = CascadeType.ALL)
    private Client client;

    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    @ManyToOne
    private TicketElement ticketElement;

    private LocalDateTime createdWhen;

    @ManyToOne(cascade = CascadeType.ALL)
    private TicketingUser createdBy;

    private String description;


    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;


    private Integer slaHours;
    @ManyToOne(cascade = CascadeType.ALL)
    private TicketingUser assignedTo;
    private LocalDateTime assignedWhen;

    @ManyToOne
    private SolutionType solutionType;

    private String solutionDescription;
    @ManyToOne(cascade = CascadeType.ALL)
    private TicketingUser closedBy;
    private LocalDateTime closedWhen;

    @ManyToOne(cascade = CascadeType.ALL)
    private TicketingUser cancelledBy;
    private LocalDateTime cancelledWhen;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<Comment> comments;


    public Optional<TicketingUser> getAssignedTo() {
        return Optional.ofNullable(assignedTo);
    }
}
