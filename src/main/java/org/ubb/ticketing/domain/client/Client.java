package org.ubb.ticketing.domain.client;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.UniqueElements;

import java.io.Serializable;
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
    @UniqueElements
    private String email;
    private String phoneNumber;

}
