package org.ubb.ticketing.domain;


import jakarta.persistence.*;
import lombok.*;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.domain.user.TicketingUser;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String commentText;

    @ManyToOne(cascade = CascadeType.ALL)
    private TicketingUser commenter;

    @ManyToOne(cascade = CascadeType.ALL)
    private ComplaintTicket ticket;

    private LocalDateTime commentedWhen;
}
