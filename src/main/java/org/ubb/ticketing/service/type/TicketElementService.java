package org.ubb.ticketing.service.type;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.ubb.ticketing.domain.TicketElement;
import org.ubb.ticketing.domain.TicketParameterStatus;
import org.ubb.ticketing.exception.TicketParameterException;
import org.ubb.ticketing.repository.TicketElementRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class TicketElementService {

    private final TicketElementRepository ticketElementRepository;
    Logger logger = LoggerFactory.getLogger(TicketElementService.class);

    public TicketElementService(TicketElementRepository ticketElementRepository) {
        this.ticketElementRepository = ticketElementRepository;
    }

    public TicketElement createTicketElement(String elementName) {
        logger.info("Creating ticket element method accessed");

        if (!ticketElementRepository.findByName(elementName).isEmpty()) {
            logger.info("Ticket element already exists");
            throw new TicketParameterException("Ticket element already exists");
        }
        TicketElement ticketElement = TicketElement
                .builder()
                .name(elementName)
                .ticketElementStatus(TicketParameterStatus.ACTIVE)
                .build();
        var savedTicketElement = ticketElementRepository.save(ticketElement);
        logger.info("Ticket element created successfully");
        return savedTicketElement;
    }

    public Set<TicketElement> getAllTicketElements() {
        return new HashSet<>(ticketElementRepository.findAll());
    }

    @Transactional
    public TicketElement deactivateTicketElement(Long id) {
        logger.info("Deactivating ticket element method accessed");
        var ticketElement = ticketElementRepository
                .findById(id).orElseThrow(() -> new TicketParameterException("No ticket element with id " + id));
        ticketElement.setTicketElementStatus(TicketParameterStatus.INACTIVE);
        return ticketElement;
    }

    @Transactional
    public TicketElement reactivateTicketElement(Long id) {
        logger.info("Reactivating ticket element method accessed");
        var ticketElement = ticketElementRepository
                .findById(id).orElseThrow(() -> new TicketParameterException("No ticket element with id " + id));
        ticketElement.setTicketElementStatus(TicketParameterStatus.ACTIVE);
        return ticketElement;
    }


}
