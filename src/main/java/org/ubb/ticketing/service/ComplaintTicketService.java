package org.ubb.ticketing.service;


import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.ubb.ticketing.domain.SolutionType;
import org.ubb.ticketing.domain.TicketStatus;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.domain.user.TicketingUser;
import org.ubb.ticketing.domain.validator.ComplaintTicketValidator;
import org.ubb.ticketing.exception.TicketNotFoundException;
import org.ubb.ticketing.repository.ComplaintTicketRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class ComplaintTicketService {


    private final ComplaintTicketRepository complaintTicketRepository;
    private final ComplaintTicketValidator complaintTicketValidator;
    private final Logger logger = LoggerFactory.getLogger(ComplaintTicketService.class);

    public ComplaintTicketService(ComplaintTicketRepository complaintTicketRepository,
                                  ComplaintTicketValidator complaintTicketValidator) {
        this.complaintTicketRepository = complaintTicketRepository;
        this.complaintTicketValidator = complaintTicketValidator;
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

    public ComplaintTicket save(ComplaintTicket complaintTicket) {
        logger.debug("save complaint ticket accessed in service");
        complaintTicketValidator.validate(complaintTicket);
        return complaintTicketRepository.save(complaintTicket);
    }

    public void deleteById(Long id) {
        logger.debug("delete complaint ticket by id accessed in service");
        complaintTicketRepository
                .findById(id).orElseThrow(
                        () -> new TicketNotFoundException("No complaint ticket with id " + id)
                );
        complaintTicketRepository.deleteById(id);
    }

    @Transactional
    public ComplaintTicket assignTicket(Long id, TicketingUser assignedTo, TicketingUser currentUser) {
        logger.debug("assignTicket complaint ticket accessed in service");
        var ticket = complaintTicketRepository
                .findById(id).orElseThrow(
                        () -> new TicketNotFoundException("No complaint ticket with id " + id)
                );

        //TODO: check if the user exists and cross save the modifications
//        var assignedToUser = userRepository
//                .findByName(assignedTo).orElseThrow(
//                        () -> new UserNotFoundException("No user with name " + assignedTo)
//                );
        //TODO: check if the current user is the user who wants to assign to himself the ticket wit any role or has supervisor role

        ticket.setAssignedTo(assignedTo);
        ticket.setTicketStatus(TicketStatus.ASSIGNED);
        return ticket;
    }


    @Transactional
    public ComplaintTicket workingOnTicket(Long id, TicketingUser currentUser) {
        logger.debug("assignTicket complaint ticket accessed in service");
        var ticket = complaintTicketRepository
                .findById(id).orElseThrow(
                        () -> new TicketNotFoundException("No complaint ticket with id " + id)
                );
        //TODO: check if the user exists and cross save the modifications
//        var assignedToUser = userRepository
//                .findByName(assignedTo).orElseThrow(
//                        () -> new UserNotFoundException("No user with name " + assignedTo)
//                );
        //TODO: check if the current user is the assigned user otherwise notify the user

        //TODO: enable editing description
        //TODO: enable editing element


        ticket.setTicketStatus(TicketStatus.IN_PROGRESS);
        return ticket;
    }


    @Transactional
    public ComplaintTicket closeTicket(Long id, TicketingUser currentUser,
                                       String solutionDescription,
                                       SolutionType solutionType) {
        logger.debug("closeTicket complaint ticket accessed in service");
        var ticket = complaintTicketRepository
                .findById(id).orElseThrow(
                        () -> new TicketNotFoundException("No complaint ticket with id " + id)
                );


        //TODO: check if the user is granted to close tickets and the ticket is assignet to teh current user

        ticket.setSolutionDescription(solutionDescription);
        ticket.setSolutionType(solutionType);

        ticket.setClosedBy(currentUser);
        ticket.setClosedWhen(LocalDateTime.now());


        //TODO: send notification about the closeing event

        ticket.setTicketStatus(TicketStatus.CLOSED);

        return ticket;
    }


}
