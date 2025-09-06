package org.ubb.ticketing.domain.customer;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
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
public class Customer implements Serializable {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID customerId;

    private String firstName;
    private String lastName;

    private String email;
    private String phoneNumber;

    @OneToMany(mappedBy = "customer")
    private List<ComplaintTicket> tickets;

}
