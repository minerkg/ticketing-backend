package org.ubb.ticketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ubb.ticketing.dto.user.TicketingUserDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private String commentText;
    private TicketingUserDto commenter;
    private Long ticketId;
    private LocalDateTime commentedWhen;
}
