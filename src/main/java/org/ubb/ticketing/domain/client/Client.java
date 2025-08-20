package org.ubb.ticketing.domain.client;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.UniqueElements;
import org.ubb.ticketing.domain.Ticket;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class Client implements Serializable {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID userId;

    private String firstName;
    private String lastName;

    private String email;
    private String phoneNumber;

    @OneToMany(mappedBy = "client")
    private List<ComplaintTicket> tickets;

}
