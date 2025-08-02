package org.ubb.ticketing.controller;


import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.ubb.ticketing.dto.PasswordChangeRequest;
import org.ubb.ticketing.dto.RoleUpdateRequest;
import org.ubb.ticketing.dto.TicketingUserDto;
import org.ubb.ticketing.dto.UserRegistrationRequest;
import org.ubb.ticketing.exception.PasswordException;
import org.ubb.ticketing.exception.UserNotFoundException;
import org.ubb.ticketing.service.user.TicketingUserService;

@RestController
@RequestMapping("/user")
public class TicketingUserController {

    private final TicketingUserService ticketingUserService;
    private final Logger logger = LoggerFactory.getLogger(TicketingUserController.class);


    public TicketingUserController(TicketingUserService ticketingUserService) {
        this.ticketingUserService = ticketingUserService;
    }


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
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody PasswordChangeRequest request, Authentication authentication) {
        try {
            ticketingUserService.changePassword(
                    authentication.getName(),
                    request.getOldPassword(),
                    request.getNewPassword());
            logger.info("Password for user {} changed successfully", authentication.getName());
            return ResponseEntity.ok()
                    .body(new ApiResponse<>("password changed", null));
        } catch (PasswordException e) {
            logger.error("changePassword internal error", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("password change failed", e.getMessage()));
        } catch (Exception e) {
            logger.error("changePassword internal error", e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("internal server error", null));
        }

    }

    @GetMapping("/all-users")
    public ResponseEntity<ApiResponse<Iterable<TicketingUserDto>>> getAllUsers() {
        try {
            var users = ticketingUserService.getAllUsers();
            return ResponseEntity.ok(new ApiResponse<>("all users", users));
        } catch (AccessDeniedException e) {
            logger.error("getAllUsers no admin role", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("admin role needed", null));
        } catch (Exception e) {
            logger.error("getAllUsers internal error", e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("internal server error", null));
        }
    }

    public ResponseEntity<ApiResponse<TicketingUserDto>> updateUserRole(
            @RequestBody RoleUpdateRequest roleUpdateRequest,
            Authentication authentication) {
        try {
            ticketingUserService.updateUserRole(authentication.getName(), roleUpdateRequest.getNewRole());
            return ResponseEntity.ok(new ApiResponse<>("user role updated", null));
        } catch (AccessDeniedException e) {
            logger.error("user role cant be updated, no admin role", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("admin role needed", null));
        } catch (UserNotFoundException e) {
            logger.error("user not found, role cant be updated", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("user not found, role cant be updated", null));
        } catch (Exception e) {
            logger.error("updateUserRole internal error", e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("internal server error", null));
        }

    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<TicketingUserDto>> getCurrentUser() {
        try {
            logger.info("getCurrentUser accessed in controller");
            return ResponseEntity
                    .ok(new ApiResponse<>("current user",
                            ticketingUserService.getCurrentUser()));
        } catch (UserNotFoundException e) {
            logger.error("getAllTickets user not found", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("user not found", null));
        } catch (Exception e) {
            logger.error("getCurrentUser internal error", e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("internal server error", null));
        }
    }

}
