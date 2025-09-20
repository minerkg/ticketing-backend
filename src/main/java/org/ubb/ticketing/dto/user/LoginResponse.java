package org.ubb.ticketing.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private TicketingUserDto user;
}
