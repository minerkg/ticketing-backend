package org.ubb.ticketing.controller;


import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.dto.*;
import org.ubb.ticketing.exception.TicketingSystemException;
import org.ubb.ticketing.service.ComplaintTicketService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/complaint-ticket")
public class ComplaintTicketController {
    private final ComplaintTicketService complaintTicketService;
    private final Logger logger = LoggerFactory.getLogger(ComplaintTicketController.class);
    private final ModelMapper modelMapper = new ModelMapper();

    public ComplaintTicketController(ComplaintTicketService complaintTicketService) {
        this.complaintTicketService = complaintTicketService;

    }

    @GetMapping()
    public ResponseEntity<ApiResponse<Set<TicketDto>>> getAllTickets() {
        try {
            var complaintTickets = complaintTicketService.getAll();
            var complaintTicketDtos = complaintTickets.stream()
                    .map(ticket -> modelMapper.map(ticket, TicketDto.class))
                    .collect(Collectors.toSet());
            return ResponseEntity.ok(new ApiResponse<>("all tickets", complaintTicketDtos));
        } catch (Exception e) {
            logger.error("getAllTickets internal error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/my-assigned-tickets")
    public ResponseEntity<ApiResponse<List<TicketDto>>> getCurrentUserAssignedTickets(Authentication authentication) {
        try {
            var usersComplaintTickets = complaintTicketService.getCurrentUserAssignedTickets(authentication);
            var usersComplaintTicketDtos = usersComplaintTickets.stream()
                    .map(ticket -> modelMapper.map(ticket, TicketDto.class))
                    .toList();
            return ResponseEntity.ok(new ApiResponse<>("user's all tickets", usersComplaintTicketDtos));
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
    public ResponseEntity<ApiResponse<TicketDto>> createTicket(@RequestBody TicketCreationRequest ticketCreationRequest,
                                                               Authentication authentication) {
        logger.info("createTicket accessed in controller");
        try {
            var complaintTicket = complaintTicketService.createTicket(ticketCreationRequest, authentication);
            var complaintTicketDto = modelMapper.map(complaintTicket, TicketDto.class);
            return ResponseEntity.ok(new ApiResponse<>("ticket created", complaintTicketDto));
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

    @PutMapping("/assign/{ticketId}")
    public ResponseEntity<ApiResponse<TicketDto>> assignTicket(@PathVariable Long ticketId,
                                                               @RequestBody TicketingUserDto assignToUserDto,
                                                               Authentication authentication) {
        logger.info("assignTicket accessed in controller");
        try {
            var assignedTicket = complaintTicketService.assignTicket(ticketId, assignToUserDto, authentication);
            logger.info("ticket with id {} assigned to user {}", ticketId, assignToUserDto.getUsername());
            var assignedTicketDto = modelMapper.map(assignedTicket, TicketDto.class);
            return ResponseEntity.ok(new ApiResponse<>("ticket assigned", assignedTicketDto));
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
    public ResponseEntity<ApiResponse<TicketDto>> closeTicket(@PathVariable Long id,
                                                                    @RequestBody TicketCloseRequest ticketCloseRequest,
                                                                    Authentication authentication) {
        logger.info("closeTicket accessed in controller");
        try {
            var closedTicket = complaintTicketService.closeTicket(id, authentication, ticketCloseRequest);
            logger.info("ticket with id {} closed", id);
            var closedTicketDto = modelMapper.map(closedTicket, TicketDto.class);
            return ResponseEntity.ok(new ApiResponse<>("ticket closed", closedTicketDto));
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
    public ResponseEntity<ApiResponse<TicketDto>> workingOnTicket(@PathVariable Long id,
                                                                        @RequestBody ComplaintTicketRequest complaintTicketRequest,
                                                                        Authentication authentication) {
        try {
            logger.info("workingOnTicket method accessed in controller");
            var updatedTicket = complaintTicketService.editTicket(id, complaintTicketRequest, authentication);
            var updatedTicketDto = modelMapper.map(updatedTicket, TicketDto.class);
            return ResponseEntity.ok(new ApiResponse<>("ticket edited", updatedTicketDto));
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

    //TODO: cancelTicket


}
