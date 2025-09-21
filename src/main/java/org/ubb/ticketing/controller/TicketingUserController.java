package org.ubb.ticketing.controller;


import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.ubb.ticketing.dto.user.*;
import org.ubb.ticketing.exception.PasswordException;
import org.ubb.ticketing.exception.TicketingSystemException;
import org.ubb.ticketing.exception.UserNotFoundException;
import org.ubb.ticketing.service.user.JwtService;
import org.ubb.ticketing.service.user.TicketingUserDetailsService;
import org.ubb.ticketing.service.user.TicketingUserService;

@RestController
@RequestMapping("/user")
public class TicketingUserController {

    private final TicketingUserService ticketingUserService;
    private final Logger logger = LoggerFactory.getLogger(TicketingUserController.class);
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final TicketingUserDetailsService ticketingUserDetailsService;


    public TicketingUserController(TicketingUserService ticketingUserService, JwtService jwtService, AuthenticationManager authenticationManager, ModelMapper modelMapper, TicketingUserDetailsService ticketingUserDetailsService) {
        this.ticketingUserService = ticketingUserService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.ticketingUserDetailsService = ticketingUserDetailsService;
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TicketingUserDto>> registerUser(@RequestBody UserRegistrationRequest registerRequest) {
        String confirmationLink = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/user/registerConfirm")
                .toUriString();
        try {
            var newUser = ticketingUserService.registerUser(registerRequest, confirmationLink);
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
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody PasswordChangeRequest request) {
        String confirmationLink = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/user/passChangeConfirm")
                .toUriString();
        try {
            ticketingUserService.changePassword(
                    request.getUsername(),
                    request.getOldPassword(),
                    request.getNewPassword(),
                    confirmationLink);
            logger.info("Password for user {} changed successfully",request.getUsername());
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


    @PutMapping("/role")
    public ResponseEntity<ApiResponse<TicketingUserDto>> updateUserRole(
            @RequestBody RoleUpdateRequest roleUpdateRequest,
            Authentication authentication) {
        try {
            ticketingUserService.updateUserRole(roleUpdateRequest.getUsername(), roleUpdateRequest.getNewRole());
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
    public ResponseEntity<ApiResponse<TicketingUserDto>> getCurrentUser(Authentication authentication) {
        try {
            logger.info("getCurrentUser accessed in controller");
            return ResponseEntity
                    .ok(new ApiResponse<>("current user",
                            ticketingUserService.getCurrentUserDto(authentication)));
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

    @PutMapping("/update-detail")
    public ResponseEntity<ApiResponse<TicketingUserDto>> updateUser(
            @RequestBody UserDetailUpdateRequest updateRequest,
            Authentication authentication) {
        try {
            var updatedUser = ticketingUserService.updateUserDetails(authentication, updateRequest);
            return ResponseEntity.ok(new ApiResponse<>("user detail updated", updatedUser));
        } catch (AccessDeniedException e) {
            logger.error("access error", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("access error", null));
        } catch (UserNotFoundException e) {
            logger.error("user not found, details cant be updated", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("user not found, cant be updated", null));
        } catch (Exception e) {
            logger.error("updateUser internal error", e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("internal server error", null));
        }

    }

    @GetMapping("/registerConfirm")
    public ResponseEntity<String> confirmUser(@RequestParam("token") String token) {
        logger.debug("confirmUser accessed in controller");
        try {
            var isConfirmed = ticketingUserService.confirmRegistration(token);
            return ResponseEntity.ok("Account confirmed! You can now log in.");
        } catch (TicketingSystemException e) {
            logger.error("confirmUser internal error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("confirmUser internal error", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/passChangeConfirm")
    public ResponseEntity<String> confirmPasswordChange(@RequestParam("token") String token) {
        logger.debug("confirmPasswordChange accessed in controller");
        try {
            var isConfirmed = ticketingUserService.confirmPasswordChange(token);
            return ResponseEntity.ok("Password change confirmed! You can now log in with your new password.");
        } catch (TicketingSystemException e) {
            logger.error("confirmPasswordChange internal error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("confirmPasswordChange internal error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            LoginResponse loginResp = ticketingUserService.login(request);

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", loginResp.getAccessToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/user/refresh")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Strict")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            return ResponseEntity.ok(
                    LoginResponse.builder()
                            .accessToken(loginResp.getAccessToken())
                            .user(loginResp.getUser())
                            .build()
            );
        } catch (BadCredentialsException e) {
            logger.error("login bad credentials", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        } catch (Exception e) {
            logger.error("login internal error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@CookieValue(name = "refreshToken") String refreshToken) {
        RefreshResponse respt = null;
        try {
            respt = ticketingUserService.refreshToken(refreshToken);
        } catch (Exception e) {
            logger.error("refreshToken internal error", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(respt);
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/user/refresh")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        return ResponseEntity.ok().build();
    }


}
