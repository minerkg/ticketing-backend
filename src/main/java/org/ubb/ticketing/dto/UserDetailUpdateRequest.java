package org.ubb.ticketing.dto;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserDetailUpdateRequest {
    private UUID userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
