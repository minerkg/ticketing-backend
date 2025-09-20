package org.ubb.ticketing.dto.user;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshResponse {
    private String accessToken;

}
