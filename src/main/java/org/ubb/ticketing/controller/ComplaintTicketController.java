package org.ubb.ticketing.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.service.ComplaintTicketService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/complaint-ticket")
public class ComplaintTicketController {
    private final ComplaintTicketService complaintTicketService;
    private final Logger logger = LoggerFactory.getLogger(ComplaintTicketController.class);

    public ComplaintTicketController(ComplaintTicketService complaintTicketService) {
        this.complaintTicketService = complaintTicketService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<Set<ComplaintTicket>>> getAllTickets() {
        try {
            var complaintTickets = complaintTicketService.getAll();
            return ResponseEntity.ok(new ApiResponse<>("all tickets", complaintTickets));
        } catch (Exception e) {
            logger.error("getAllTickets internal error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/my-assigned-tickets")
    public ResponseEntity<ApiResponse<List<ComplaintTicket>>> getCurrentUserAssignedTickets(Authentication authentication) {
        try {
            var usersComplaintTickets = complaintTicketService.getCurrentUserAssignedTickets(authentication);
            return ResponseEntity.ok(new ApiResponse<>("user's all tickets", usersComplaintTickets));
        } catch (AuthenticationException e) {
            logger.error("getCurrentUserTickets no authentication", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("no authentication", null));
        } catch (Exception e) {
            logger.error("getCurrentUserTickets internal error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

//    public ResponseEntity<ApiResponse<ComplaintTicket>> createTicket(Authentication authentication) {
//
//    }
//
//    public ResponseEntity<ApiResponse<ComplaintTicket>> assignTicket(Long id, Authentication authentication) {
//
//    }
//
//    public ResponseEntity<ApiResponse<ComplaintTicket>> closeTicket(Long id, Authentication authentication) {
//
//    }
//
//    public ResponseEntity<ApiResponse<ComplaintTicket>> workingOnTicket(Long id, Authentication authentication) {
//
//    }





}
