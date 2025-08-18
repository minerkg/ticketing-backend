package org.ubb.ticketing.controller;


import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.ubb.ticketing.dto.TicketDto;
import org.ubb.ticketing.exception.TicketingSystemException;
import org.ubb.ticketing.service.CommentService;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final Logger logger = LoggerFactory.getLogger(CommentController.class);
    private final ModelMapper modelMapper;

    public CommentController(CommentService commentService, ModelMapper modelMapper) {
        this.commentService = commentService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<TicketDto>> addComment(@PathVariable Long ticketId,
                                                             @RequestBody String commentText,
                                                             Authentication authentication) {
        logger.debug("addComment accessed in controller");
        try {
            var ticket = commentService.addComment(ticketId, commentText, authentication);
            var ticketDto = modelMapper.map(ticket, TicketDto.class);
            logger.debug("comment {} added to the ticket {}", commentText, ticketId);
            return ResponseEntity.ok(new ApiResponse<>("comment added to the ticket", ticketDto));
        } catch (TicketingSystemException e) {
            logger.error("addComment internal error", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(e.getMessage(), null));
        } catch (AuthenticationException e) {
            logger.error("addComment no authentication", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("no authentication", null));
        } catch (Exception e) {
            logger.error("addComment internal server error", e);
            return ResponseEntity.internalServerError().build();
        }

    }
}
