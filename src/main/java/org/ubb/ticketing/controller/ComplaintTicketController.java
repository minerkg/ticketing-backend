package org.ubb.ticketing.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ubb.ticketing.Exceptions.TicketingSystemException;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.service.ComplaintTicketService;

import java.util.Set;

@RestController
@RequestMapping("/complaint-tickets")
public class ComplaintTicketController {
    private final ComplaintTicketService complaintTicketService;
    private final Logger logger = LoggerFactory.getLogger(ComplaintTicketController.class);

    public ComplaintTicketController(ComplaintTicketService complaintTicketService) {
        this.complaintTicketService = complaintTicketService;
    }

    public ResponseEntity<ApiResponse<Set<ComplaintTicket>>> getAllTickets() {
        try {
            var complaintTickets = complaintTicketService.getAll();
            return ResponseEntity.ok(new ApiResponse<>("all tickets", complaintTickets));
        } catch (TicketingSystemException e) {
            logger.error("getAllTickets intrnal", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
