package org.ubb.ticketing.converter;

import org.springframework.stereotype.Component;
import org.ubb.ticketing.domain.user.TicketingUser;
import org.ubb.ticketing.dto.user.TicketingUserDto;

@Component
public class UserDtoConverter extends BaseConverter<TicketingUser, TicketingUserDto> {
    @Override
    public TicketingUser convertDtoToModel(TicketingUserDto dto) {
        return TicketingUser.builder()
                .username(dto.getUsername())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    @Override
    public TicketingUserDto convertModelToDto(TicketingUser user) {
        return TicketingUserDto.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .userRole(user.getUserRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
