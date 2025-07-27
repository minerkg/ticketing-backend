package org.ubb.ticketing;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.ubb.ticketing.domain.TicketFactory;
import org.ubb.ticketing.domain.TicketType;
import org.ubb.ticketing.domain.complaint.ComplaintTicket;
import org.ubb.ticketing.service.ComplaintTicketService;

@Component
public class AddInitialData implements CommandLineRunner {

    private final ComplaintTicketService complaintTicketService;

    public AddInitialData(ComplaintTicketService complaintTicketService) {
        this.complaintTicketService = complaintTicketService;
    }

    @Override
    public void run(String... args) throws Exception {
        ComplaintTicket complaintTicket = ComplaintTicket.builder().build();
        complaintTicketService.save(complaintTicket);

    }
}
