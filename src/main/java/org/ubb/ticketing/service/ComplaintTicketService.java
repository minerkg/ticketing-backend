package org.ubb.ticketing.service;


import org.springframework.stereotype.Service;
import org.ubb.ticketing.repository.ComplaintTicketRepository;

@Service
public class ComplaintTicketService {


    private final ComplaintTicketRepository complaintTicketRepository;

    public ComplaintTicketService(ComplaintTicketRepository complaintTicketRepository) {
        this.complaintTicketRepository = complaintTicketRepository;
    }



}
