package org.ubb.ticketing.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.ubb.ticketing.converter.UserDtoConverter;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.dto.ComplaintTicketRequest;
import org.ubb.ticketing.dto.TicketCloseRequest;
import org.ubb.ticketing.dto.TicketCreationRequest;
import org.ubb.ticketing.dto.TicketingUserDto;
import org.ubb.ticketing.exception.TicketingSystemException;
import org.ubb.ticketing.service.ComplaintTicketService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/complaint-ticket")
public class ComplaintTicketController {
    private final ComplaintTicketService complaintTicketService;
    private final Logger logger = LoggerFactory.getLogger(ComplaintTicketController.class);
    private final UserDtoConverter userDtoConverter;

    public ComplaintTicketController(ComplaintTicketService complaintTicketService, UserDtoConverter userDtoConverter) {
        this.complaintTicketService = complaintTicketService;
        this.userDtoConverter = userDtoConverter;
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

    @PostMapping()
    public ResponseEntity<ApiResponse<ComplaintTicket>> createTicket(@RequestBody TicketCreationRequest ticketCreationRequest, Authentication authentication) {
        logger.info("createTicket accessed in controller");
        try {
            var complaintTicket = complaintTicketService.createTicket(ticketCreationRequest, authentication);
            return ResponseEntity.ok(new ApiResponse<>("ticket created", complaintTicket));
        } catch (AuthenticationException e) {
            logger.error("createTicket no authentication", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("no authentication", null));
        } catch (TicketingSystemException e) {
            logger.error("createTicket internal error", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            logger.error("createTicket internal error", e);
            return ResponseEntity.internalServerError().build();
        }

    }

    @PutMapping("/assign/{id}")
    public ResponseEntity<ApiResponse<ComplaintTicket>> assignTicket(@PathVariable Long id,
                                                                     @RequestBody TicketingUserDto assignToUserDto,
                                                                     Authentication authentication) {
        logger.info("assignTicket accessed in controller");
        try {
            var assignToUser = userDtoConverter.convertDtoToModel(assignToUserDto);
            var assignedTicket = complaintTicketService.assignTicket(id, assignToUser, authentication);
            logger.info("ticket with id {} assigned to user {}", id, assignToUserDto.getUsername());
            return ResponseEntity.ok(new ApiResponse<>("ticket assigned", assignedTicket));
        } catch (TicketingSystemException e) {
            logger.error("assignTicket internal error", e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));
        } catch (AuthenticationException e) {
            logger.error("assignTicket no authentication", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("no authentication", null));
        } catch (Exception e) {
            logger.error("assignTicket internal error", e);
            return ResponseEntity.internalServerError().build();
        }

    }

    @PutMapping("/close/{id}")
    public ResponseEntity<ApiResponse<ComplaintTicket>> closeTicket(@PathVariable Long id,
                                                                    @RequestBody TicketCloseRequest ticketCloseRequest,
                                                                    Authentication authentication) {
        logger.info("closeTicket accessed in controller");
        try {
            var closedTicket = complaintTicketService.closeTicket(id, authentication, ticketCloseRequest);
            logger.info("ticket with id {} closed", id);
            return ResponseEntity.ok(new ApiResponse<>("ticket closed", closedTicket));
        } catch (TicketingSystemException e) {
            logger.error("closeTicket internal error", e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));
        } catch (AuthenticationException e) {
            logger.error("closeTicket no authentication", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("no authentication", null));
        } catch (Exception e) {
            logger.error("closeTicket internal server error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<ApiResponse<ComplaintTicket>> workingOnTicket(@PathVariable Long id,
                                                                        @RequestBody ComplaintTicketRequest complaintTicketRequest,
                                                                        Authentication authentication) {
        try {
            logger.info("workingOnTicket method accessed in controller");
            var updatedTicket = complaintTicketService.editTicket(id, complaintTicketRequest, authentication);
            return ResponseEntity.ok(new ApiResponse<>("ticket edited", updatedTicket));
        } catch (TicketingSystemException e) {
            logger.error("workingOnTicket internal error", e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), null));
        } catch (AuthenticationException e) {
            logger.error("workingOnTicket no authentication", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("no authentication", null));
        } catch (Exception e) {
            logger.error("workingOnTicket internal server error", e);
            return ResponseEntity.internalServerError().build();
        }
    }


}
