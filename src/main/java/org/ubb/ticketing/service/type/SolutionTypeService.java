package org.ubb.ticketing.service.type;


import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.ubb.ticketing.domain.SolutionType;
import org.ubb.ticketing.domain.TicketElement;
import org.ubb.ticketing.domain.TicketParameterStatus;
import org.ubb.ticketing.exception.TicketParameterException;
import org.ubb.ticketing.repository.SolutionTypeRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class SolutionTypeService {

    private final SolutionTypeRepository solutionTypeRepository;
    private final Logger logger = LoggerFactory.getLogger(SolutionTypeService.class);


    public SolutionTypeService(SolutionTypeRepository solutionTypeRepository) {
        this.solutionTypeRepository = solutionTypeRepository;
    }

    public SolutionType createSolutionType(String solutionTypeName) {
        logger.info("Creating solution type method accessed");

        if (!solutionTypeRepository.findByName(solutionTypeName).isEmpty()) {
            logger.info("Solution type already exists");
            throw new TicketParameterException("Solution type already exists");
        }
        SolutionType solutionType = SolutionType
                .builder()
                .name(solutionTypeName)
                .solutionTypeStatus(TicketParameterStatus.ACTIVE)
                .build();
        var savedSolutionType = solutionTypeRepository.save(solutionType);
        logger.info("Solution type created successfully");
        return savedSolutionType;
    }

    public Set<SolutionType> getSolutionTypes() {
        return new HashSet<>(solutionTypeRepository.findAll());
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
