package org.ubb.ticketing.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ubb.ticketing.domain.user.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketingUserDto {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole userRole;
    private LocalDateTime createdAt;
    private boolean accountEnabled;

}
