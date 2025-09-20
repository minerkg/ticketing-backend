package org.ubb.ticketing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.ubb.ticketing.domain.TicketStatus;
import org.ubb.ticketing.repository.ComplaintTicketRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChartDataService {

    private final ComplaintTicketRepository ticketRepository;
    private final Logger logger = LoggerFactory.getLogger(ChartDataService.class);

    public ChartDataService(ComplaintTicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public interface StatusCount {
        TicketStatus getStatus();

        Long getCount();
    }


    private Map<TicketStatus, Long> getComplaintTicketStatusStats() {
        return ticketRepository.countComplaintTicketByTicketStatus()
                .stream()
                .collect(Collectors.toMap(StatusCount::getStatus, StatusCount::getCount));
    }


}
