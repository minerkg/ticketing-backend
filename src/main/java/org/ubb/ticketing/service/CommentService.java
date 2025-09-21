package org.ubb.ticketing.service;


import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.ubb.ticketing.domain.Comment;
import org.ubb.ticketing.domain.TicketStatus;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.exception.TicketNotFoundException;
import org.ubb.ticketing.exception.TicketingSystemException;
import org.ubb.ticketing.exception.UserNotFoundException;
import org.ubb.ticketing.repository.ComplaintTicketRepository;
import org.ubb.ticketing.repository.TicketingUserRepository;
import org.ubb.ticketing.service.notification.NotificationService;
import org.ubb.ticketing.service.user.TicketingUserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {


    private final ComplaintTicketRepository complaintTicketRepository;
    private final TicketingUserRepository ticketingUserRepository;
    private final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final TicketingUserService ticketingUserService;
    private final NotificationService notificationService;

    public CommentService(ComplaintTicketRepository complaintTicketRepository, TicketingUserRepository ticketingUserRepository, TicketingUserService ticketingUserService, NotificationService notificationService) {
        this.complaintTicketRepository = complaintTicketRepository;
        this.ticketingUserRepository = ticketingUserRepository;
        this.ticketingUserService = ticketingUserService;
        this.notificationService = notificationService;
    }


    @Transactional
    public ComplaintTicket addComment(Long ticketId, String commentText, Authentication authentication) {
        logger.debug("addComment complaint ticket accessed in service");

        var currentUser = ticketingUserService.getCurrentUser(authentication);

        var ticket = complaintTicketRepository
                .findById(ticketId).orElseThrow(
                        () -> new TicketNotFoundException("No complaint ticket with id " + ticketId)
                );

        if (ticket.getTicketStatus() == TicketStatus.CLOSED
                || ticket.getTicketStatus() == TicketStatus.CANCELLED) {
            throw new TicketingSystemException("You are not allowed to add comments to " +
                    "this ticket because it is already closed or cancelled. ");
        }

        var currentUserFromRepo = ticketingUserRepository
                .findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new UserNotFoundException("No user with name " + currentUser.getUsername()));

        var newComment = Comment.builder()
                .commenter(currentUserFromRepo)
                .commentText(commentText)
                .commentedWhen(LocalDateTime.now())
                .build();
        if (ticket.getComments() == null) {
            ticket.setComments(List.of(newComment));
        } else {
            List<Comment> comments = ticket.getComments();
            comments.add(newComment);
            ticket.setComments(comments);
        }
        newComment.setTicket(ticket);
        logger.debug("Comment added to ticket {}", ticket.getTicketId());

        notificationService.notifyCommented(ticket, newComment);

        return ticket;
    }


}
