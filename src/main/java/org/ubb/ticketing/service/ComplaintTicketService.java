package org.ubb.ticketing.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.exception.TicketNotFoundException;
import org.ubb.ticketing.repository.ComplaintTicketRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class ComplaintTicketService {


    private final ComplaintTicketRepository complaintTicketRepository;
    private final Logger logger = LoggerFactory.getLogger(ComplaintTicketService.class);

    public ComplaintTicketService(ComplaintTicketRepository complaintTicketRepository) {
        this.complaintTicketRepository = complaintTicketRepository;
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


}
