package org.ubb.ticketing.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ubb.ticketing.domain.SolutionType;
import org.ubb.ticketing.domain.TicketElement;
import org.ubb.ticketing.exception.TicketParameterException;
import org.ubb.ticketing.service.type.SolutionTypeService;

import java.util.Set;

@RestController
@RequestMapping("/solution-type")
public class SolutionTypeController {


    private final SolutionTypeService solutionTypeService;
    private final Logger logger = LoggerFactory.getLogger(SolutionTypeController.class);
    public SolutionTypeController(SolutionTypeService solutionTypeService) {
        this.solutionTypeService = solutionTypeService;
    }


    @GetMapping("")
    public ResponseEntity<ApiResponse<Set<SolutionType>>> getAllSolutionTypes() {
        logger.info("getAllSolutionTypes accessed in controller");
        try {
            var solutionTypes = solutionTypeService.getSolutionTypes();
            return ResponseEntity.ok(new ApiResponse<>("all solution types", solutionTypes));
        } catch (Exception e) {
            logger.error("getAllSolutionTypes internal error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<SolutionType>> createSolutionType(@RequestParam String solutionName) {
        logger.info("createSolutionType accessed in controller");
        try {
            var solutionType = solutionTypeService.createSolutionType(solutionName);
            return ResponseEntity.ok(new ApiResponse<>("solution type created", solutionType));
        } catch (TicketParameterException e) {
            logger.error("createSolutionType parameter error", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("solution type already exists", null));
        } catch (Exception e) {
            logger.error("createSolutionType internal error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ApiResponse<SolutionType>> deactivateSolutionType(Long id) {
        logger.info("deactivateSolutionType accessed in controller");
        try {
            var solutionType = solutionTypeService.deactivateSolutionType(id);
            return ResponseEntity
                    .ok(new ApiResponse<>("solution type deactivated", solutionType));
        } catch (TicketParameterException e) {
            logger.error("solution type not found", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("solution type not found", null));
        } catch (Exception e) {
            logger.error("deactivateSolutionType internal error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/reactivate")
    public ResponseEntity<ApiResponse<SolutionType>> reactivateSolutionType(Long id) {
        logger.info("reactivateSolutionType accessed in controller");
        try {
            var solutionType = solutionTypeService.reactivateSolutionType(id);
            return ResponseEntity.ok(new ApiResponse<>("solution type reactivated", solutionType));
        } catch (TicketParameterException e) {
            logger.error("solution type not found", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("solution type not found", null));
        } catch (Exception e) {
            logger.error("reactivateSolutionType internal error", e);
            return ResponseEntity.internalServerError().build();
        }
    }






}
