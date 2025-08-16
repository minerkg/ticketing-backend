package org.ubb.ticketing.service;


import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.ubb.ticketing.domain.TicketFactory;
import org.ubb.ticketing.domain.TicketStatus;
import org.ubb.ticketing.domain.TicketType;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.domain.user.TicketingUser;
import org.ubb.ticketing.domain.validator.ComplaintTicketValidator;
import org.ubb.ticketing.dto.ComplaintTicketRequest;
import org.ubb.ticketing.dto.TicketCloseRequest;
import org.ubb.ticketing.dto.TicketCreationRequest;
import org.ubb.ticketing.dto.TicketingUserDto;
import org.ubb.ticketing.exception.TicketNotFoundException;
import org.ubb.ticketing.exception.TicketingSystemException;
import org.ubb.ticketing.exception.UserNotFoundException;
import org.ubb.ticketing.repository.ComplaintTicketRepository;
import org.ubb.ticketing.repository.TicketElementRepository;
import org.ubb.ticketing.repository.TicketingUserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ComplaintTicketService {


    private final ComplaintTicketRepository complaintTicketRepository;
    private final ComplaintTicketValidator complaintTicketValidator;
    private final Logger logger = LoggerFactory.getLogger(ComplaintTicketService.class);
    private final TicketingUserRepository ticketingUserRepository;
    private final TicketElementRepository ticketElementRepository;

    public ComplaintTicketService(ComplaintTicketRepository complaintTicketRepository,
                                  ComplaintTicketValidator complaintTicketValidator,
                                  TicketingUserRepository ticketingUserRepository,
                                  TicketElementRepository ticketElementRepository) {
        this.complaintTicketRepository = complaintTicketRepository;
        this.complaintTicketValidator = complaintTicketValidator;
        this.ticketingUserRepository = ticketingUserRepository;
        this.ticketElementRepository = ticketElementRepository;
    }


    public Set<ComplaintTicket> getAll() {
        logger.debug("get all complaint tickets accessed in service");
        return new HashSet<>(complaintTicketRepository.findAll());
    }

    public ComplaintTicket getById(Long id) {
        logger.debug("get complaint ticket by id accessed in service");
        return complaintTicketRepository
                .findById(id)
                .orElseThrow(
                        () -> new TicketNotFoundException("No complaint ticket with id " + id)
                );
    }

    @Transactional
    public ComplaintTicket createTicket(TicketCreationRequest ticketRequest, Authentication authentication) {
        logger.debug("create complaint ticket accessed in service");
        var currentUser = (TicketingUser) authentication.getPrincipal();
        logger.debug("Current user: {}", currentUser.getUsername());
        var user = ticketingUserRepository.findByUsername(currentUser.getUsername()).orElseThrow(
                () -> new UserNotFoundException("User not found with username: " + currentUser.getUsername())
        );
        var complaintTicket = (ComplaintTicket) TicketFactory.createNewTicket(TicketType.COMPLAINT, user);

        Errors errors = new BeanPropertyBindingResult(complaintTicket, "complaintTicket");
        complaintTicketValidator.validate(complaintTicket, errors);
        if (errors.hasErrors()) {
            throw new ValidationException("Complaint Ticket validation failed: " + errors.getAllErrors());
        }
        complaintTicket.setTicketElement(ticketElementRepository
                .findByName(ticketRequest.getTicketElementName()).getFirst());
        complaintTicket.setDescription(ticketRequest.getDescription());


        return complaintTicketRepository.save(complaintTicket);
    }


//    public void cancelById(Long id) {
//        logger.debug("delete complaint ticket by id accessed in service");
//        complaintTicketRepository
//                .findById(id).orElseThrow(
//                        () -> new TicketNotFoundException("No complaint ticket with id " + id)
//                );
//        complaintTicketRepository.deleteById(id);
//    }

    @Transactional
    public ComplaintTicket assignTicket(Long ticketId, TicketingUserDto assignedToDto, Authentication authentication) {
        logger.debug("assignTicket complaint ticket accessed in service");
        var currentUser = (TicketingUser) authentication.getPrincipal();

        var ticket = complaintTicketRepository
                .findById(ticketId).orElseThrow(
                        () -> new TicketNotFoundException("No complaint ticket with id " + ticketId)
                );

        if (ticket.getTicketStatus() == TicketStatus.CLOSED) {
            throw new TicketingSystemException("You are not allowed to work on this ticket because it is already closed. ");
        }

        var assignedToUser = ticketingUserRepository.findByUsername(assignedToDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("No user with name " + assignedToDto.getUsername()));

        //check if the current user is the user who wants to assign to himself the ticket with any role or has supervisor role
        if (!currentUser.equals(assignedToUser) &&
                currentUser.getAuthorities().stream()
                        .noneMatch(a -> a.getAuthority().equals("ROLE_SUPERVISOR")
                                || a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new TicketingSystemException("You are not allowed to assign this ticket to " + assignedToUser.getUsername() +
                    " because you are not the assigned user or you are not an admin.");
        }

        ticket.setAssignedTo(assignedToUser);
        if (ticket.getTicketStatus() != TicketStatus.ASSIGNED) {
            ticket.setTicketStatus(TicketStatus.ASSIGNED);
        }
        ticket.setAssignedWhen(LocalDateTime.now());

        //TODO: notify user about the new assigned ticket on hime/her

        return ticket;
    }


    @Transactional
    public ComplaintTicket editTicket(Long id, ComplaintTicketRequest updatedComplaintTicketRequest, Authentication authentication) {
        logger.debug("workingOnTicket complaint ticket accessed in service");
        var ticket = complaintTicketRepository
                .findById(id).orElseThrow(
                        () -> new TicketNotFoundException("No complaint ticket with id " + id)
                );

        if (ticket.getTicketStatus() == TicketStatus.CLOSED) {
            throw new TicketingSystemException("You are not allowed to work on this ticket because it is already closed. ");
        }

        //check if the current user is the assigned user otherwise notify the user
        if (!ticket.getAssignedTo().equals(authentication.getPrincipal())) {
            throw new TicketingSystemException("You are not allowed to work on this ticket because " +
                    "you are not assigned to it. ");
        }

        ticket.setTicketElement(updatedComplaintTicketRequest.getTicketElement());
        ticket.setDescription(updatedComplaintTicketRequest.getDescription());
        ticket.setTicketStatus(TicketStatus.IN_PROGRESS);

        return ticket;
    }


    @Transactional
    public ComplaintTicket closeTicket(Long id, Authentication authentication,
                                       TicketCloseRequest ticketCloseRequest) {
        logger.debug("closeTicket complaint ticket accessed in service");
        var currentUser = (TicketingUser) authentication.getPrincipal();

        var ticket = complaintTicketRepository
                .findById(id).orElseThrow(
                        () -> new TicketNotFoundException("No complaint ticket with id " + id)
                );

        if (ticket.getTicketStatus() == TicketStatus.CLOSED) {
            throw new TicketingSystemException("You are not allowed to work on this ticket because it is already closed. ");
        }

        if (!ticket.getAssignedTo().equals(currentUser))
            throw new TicketingSystemException("You are not allowed to close this ticket because you are not " +
                    "assigned to it. ");

        ticket.setSolutionDescription(ticketCloseRequest.getSolutionDescription());
        ticket.setSolutionType(ticketCloseRequest.getSolutionType());
        ticket.setClosedBy(currentUser);
        ticket.setClosedWhen(LocalDateTime.now());

        //TODO: send notification about the closeing event

        ticket.setTicketStatus(TicketStatus.CLOSED);

        return ticket;
    }


    public List<ComplaintTicket> getCurrentUserAssignedTickets(Authentication authentication) {
        logger.debug("getCurrentUserTickets complaint ticket accessed in service");
        var currentUser = (TicketingUser) authentication.getPrincipal();
        return complaintTicketRepository.findAll()
                .stream()
                .filter(ct -> ct.getAssignedTo().isPresent()
                        && ct.getAssignedTo().get().getUserId().equals(currentUser.getUserId()))
                .toList();

    }
}
