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
    public SolutionType deactivateSolutionType(Long id) {
        logger.info("Deactivating SolutionType method accessed");
        var solutionType = solutionTypeRepository
                .findById(id).orElseThrow(() -> new TicketParameterException("No solution type with id " + id));
        solutionType.setSolutionTypeStatus(TicketParameterStatus.INACTIVE);
        return solutionType;
    }

    @Transactional
    public SolutionType reactivateSolutionType(Long id) {
        logger.info("Reactivating SolutionType method accessed");
        var solutionType = solutionTypeRepository
                .findById(id).orElseThrow(() -> new TicketParameterException("No solution type with id " + id));
        solutionType.setSolutionTypeStatus(TicketParameterStatus.ACTIVE);
        return solutionType;
    }

}
