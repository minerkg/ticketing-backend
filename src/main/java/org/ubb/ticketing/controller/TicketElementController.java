package org.ubb.ticketing.controller;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ubb.ticketing.domain.TicketElement;
import org.ubb.ticketing.exception.TicketParameterException;
import org.ubb.ticketing.service.type.TicketElementService;

import java.util.Set;

@RestController
@RequestMapping("/ticket-element")
public class TicketElementController {

    private final TicketElementService ticketElementService;
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(TicketElementController.class);

    public TicketElementController(TicketElementService ticketElementService) {
        this.ticketElementService = ticketElementService;
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<Set<TicketElement>>> getAllTicketElements() {
        logger.info("getAllTicketElements accessed in controller");
        try {
            var ticketElements = ticketElementService.getAllTicketElements();
            return ResponseEntity.ok(new ApiResponse<>("all ticket elements", ticketElements));
        } catch (Exception e) {
            logger.error("getAllTicketElements internal error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<TicketElement>> createTicketElement(@RequestParam String elementName) {
        logger.info("createTicketElement accessed in controller");
        try {
            var ticketElement = ticketElementService.createTicketElement(elementName);
            return ResponseEntity.ok(new ApiResponse<>("ticket element created", ticketElement));
        } catch (TicketParameterException e) {
            logger.error("createTicketElement parameter error", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("ticket element already exists", null));
        } catch (Exception e) {
            logger.error("createTicketElement internal error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ApiResponse<TicketElement>> deactivateTicketElement(Long id) {
        logger.info("deactivateTicketElement accessed in controller");
        try {
            var ticketElement = ticketElementService.deactivateTicketElement(id);
            return ResponseEntity.ok(new ApiResponse<>("ticket element deactivated", ticketElement));
        } catch (TicketParameterException e) {
            logger.error("ticketing element not found", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("ticketing element not found", null));
        } catch (Exception e) {
            logger.error("deactivateTicketElement internal error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/reactivate")
    public ResponseEntity<ApiResponse<TicketElement>> reactivateTicketElement(Long id) {
        logger.info("reactivateTicketElement accessed in controller");
        try {
            var ticketElement = ticketElementService.reactivateTicketElement(id);
            return ResponseEntity.ok(new ApiResponse<>("ticket element reactivated", ticketElement));
        } catch (TicketParameterException e) {
            logger.error("ticketing element not found", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("ticketing element not found", null));
        } catch (Exception e) {
            logger.error("reactivateTicketElement internal error", e);
            return ResponseEntity.internalServerError().build();
        }
    }


}
