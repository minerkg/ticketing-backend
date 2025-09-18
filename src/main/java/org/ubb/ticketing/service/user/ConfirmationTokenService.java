package org.ubb.ticketing.service.user;

import org.springframework.stereotype.Service;
import org.ubb.ticketing.domain.user.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmationTokenService {

    public ConfirmationToken generateToken(UUID userId) {
        return ConfirmationToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusHours(24))
                .userId(userId)
                .build();
    }


}
