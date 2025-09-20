package org.ubb.ticketing.dto.user;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class UserRegistrationRequest {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;

}
