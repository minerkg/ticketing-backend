package org.ubb.ticketing.controller;


import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.ubb.ticketing.dto.TicketingUserDto;
import org.ubb.ticketing.dto.UserRegistrationRequest;
import org.ubb.ticketing.exception.PasswordException;
import org.ubb.ticketing.service.user.TicketingUserService;

@RestController
@RequestMapping("/user")
public class TicketingUserController {

    private final TicketingUserService ticketingUserService;
    private final Logger logger = LoggerFactory.getLogger(TicketingUserController.class);

    public TicketingUserController(TicketingUserService ticketingUserService) {
        this.ticketingUserService = ticketingUserService;
    }


//    @GetMapping("/public/auth")
//    public ResponseEntity<ApiResponse<String>> auth(@RequestBody String username, @RequestBody String password) {
//
//    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TicketingUserDto>> registerUser(@RequestBody UserRegistrationRequest registerRequest) {
        try {
            var newUser = ticketingUserService.registerUser(registerRequest);
            logger.info("User {} created successfully", newUser.getUsername());
            return ResponseEntity.ok()
                    .body(new ApiResponse<>("user created", newUser));
        } catch (ValidationException e) {
            logger.error("registerUser validation failed", e);
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>("user already exists", null));
        } catch (PasswordException e) {
            logger.error("registerUser password validation failed", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("password is weak", null));
        } catch (Exception e) {
            logger.error("registerUser internal error", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("user creation failed", null));
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request, Authentication authentication) {
        try {
            System.out.println(request);
            userService.changePassword(authentication.getName(), request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok().body(new ApiResponse<>("password changed", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("password change failed", e.getMessage()));
        }

    }


}
