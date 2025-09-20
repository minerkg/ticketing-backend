package org.ubb.ticketing.service.user;



import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.ubb.ticketing.converter.UserDtoConverter;
import org.ubb.ticketing.domain.user.ConfirmationToken;
import org.ubb.ticketing.domain.user.TicketingUser;
import org.ubb.ticketing.domain.user.UserRole;
import org.ubb.ticketing.domain.validator.PasswordValidator;
import org.ubb.ticketing.domain.validator.TicketingUserDetailUpdateValidator;
import org.ubb.ticketing.domain.validator.TicketingUserValidator;
import org.ubb.ticketing.dto.user.TicketingUserDto;
import org.ubb.ticketing.dto.user.UserDetailUpdateRequest;
import org.ubb.ticketing.dto.user.UserRegistrationRequest;
import org.ubb.ticketing.exception.PasswordException;
import org.ubb.ticketing.exception.TicketingSystemException;
import org.ubb.ticketing.exception.UserAlreadyExistsException;
import org.ubb.ticketing.exception.UserNotFoundException;
import org.ubb.ticketing.repository.TicketingUserRepository;
import org.ubb.ticketing.repository.TokenRepository;
import org.ubb.ticketing.service.notification.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketingUserService {


    private final TicketingUserRepository ticketingUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final TicketingUserValidator ticketingUserValidator;
    private final TicketingUserDetailUpdateValidator ticketingUserUpdateValidator;
    private final PasswordValidator passwordValidator;
    private final Logger logger = LoggerFactory.getLogger(TicketingUserService.class);
    private final UserDtoConverter userDtoConverter;
    private final TokenRepository tokenRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final NotificationService notificationService;
    private final JwtService jwtService;

    public TicketingUserService(TicketingUserRepository ticketingUserRepository, PasswordEncoder passwordEncoder, TicketingUserValidator ticketingUserValidator, TicketingUserDetailUpdateValidator ticketingUserUpdateValidator, PasswordValidator passwordValidator, UserDtoConverter userDtoConverter, TokenRepository tokenRepository, ConfirmationTokenService confirmationTokenService, NotificationService notificationService, JwtService jwtService) {
        this.ticketingUserRepository = ticketingUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.ticketingUserValidator = ticketingUserValidator;
        this.ticketingUserUpdateValidator = ticketingUserUpdateValidator;
        this.passwordValidator = passwordValidator;
        this.userDtoConverter = userDtoConverter;
        this.tokenRepository = tokenRepository;
        this.confirmationTokenService = confirmationTokenService;
        this.notificationService = notificationService;
        this.jwtService = jwtService;
    }


//    public String login(String username, String password) {
//        TicketingUser user = ticketingUserRepository.findByUsername(username)
//                .orElseThrow(() -> new UserNotFoundException("Invalid username or password"));
//
//        if (!user.isAccountEnabled()) {
//            throw new DisabledException("Account not confirmed");
//        }
//
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new BadCredentialsException("Invalid username or password");
//        }
//
//        return jwtService.generateToken(user.getUsername(), user.getUserRole().name());
//    }


    @Transactional
    public TicketingUserDto registerUser(UserRegistrationRequest userRequest, String baseUrl) {
        logger.info("Registering user {}", userRequest.getUsername());
        Errors userErrors = new BeanPropertyBindingResult(userRequest, "userRequest");
        ticketingUserValidator.validate(userRequest, userErrors);

        if (userErrors.hasErrors()) {
            throw new ValidationException("User registration validation failed: " + userErrors.getAllErrors());
        }

        Errors passwordErrors = new BeanPropertyBindingResult(userRequest.getPassword(), "newPassword");
        passwordValidator.validate(userRequest.getPassword(), passwordErrors);
        if (passwordErrors.hasErrors()) {
            throw new PasswordException("Password does not meet security requirements.");
        }

        try {
            TicketingUser newUser = TicketingUser.builder()
                    .username(userRequest.getUsername())
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .email(userRequest.getEmail())
                    .userRole(UserRole.USER)
                    .accountEnabled(false)
                    .build();

            var savedNewUser = ticketingUserRepository.save(newUser);
            var token = confirmationTokenService.generateToken(savedNewUser.getUserId());
            tokenRepository.save(token);

            notificationService.notifyTokenGenerated(savedNewUser, token, baseUrl);

            return userDtoConverter.convertModelToDto(savedNewUser);

        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("A user with the provided email or username already exists", e);
        }
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateUserRole(String username, UserRole newRole) throws AccessDeniedException {
        TicketingUser user = ticketingUserRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        user.setUserRole(newRole);
    }


    @PreAuthorize("isAuthenticated()")
    public void changePassword(String username, String currentPassword, String newPassword) {
        TicketingUser user = ticketingUserRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        Errors errors = new BeanPropertyBindingResult(newPassword, "newPassword");
        passwordValidator.validate(newPassword, errors);
        if (errors.hasErrors()) {
            throw new PasswordException("Password does not meet security requirements.");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PasswordException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        ticketingUserRepository.save(user);
        logger.info("Password for user {} changed successfully", username);
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public List<TicketingUserDto> getAllUsers() {
        return ticketingUserRepository.findAllUsersWithoutPassword();
    }

    public TicketingUserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.oauth2.jwt.Jwt jwt =
                (org.springframework.security.oauth2.jwt.Jwt) authentication.getPrincipal();

        String username = jwt.getClaim("sub");

        TicketingUser currentUser = ticketingUserRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        return userDtoConverter.convertModelToDto(currentUser);
    }



    @Transactional
    public TicketingUserDto updateUserDetails(Authentication authentication, UserDetailUpdateRequest userRequest) {
        logger.info("Updating user details for user {}", userRequest.getUsername());
        Errors userErrors = new BeanPropertyBindingResult(userRequest, "userRequest");

        var currentUser = (TicketingUser) authentication.getPrincipal();
        if (currentUser.getUserRole() != UserRole.ADMIN
                && !currentUser.getUserId().equals(userRequest.getUserId())) {
            throw new AccessDeniedException("You are not allowed to update this user's details.");
        }

        ticketingUserUpdateValidator.validate(userRequest, userErrors);
        if (userErrors.hasErrors()) {
            throw new ValidationException("User detail update validation failed: " + userErrors.getAllErrors());
        }

        TicketingUser user = ticketingUserRepository.findByUserId(userRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + userRequest.getUsername()));

        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());

        return userDtoConverter.convertModelToDto(user);

    }

    @Transactional
    public boolean confirmRegistration(String token) {
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new TicketingSystemException("Invalid token"));

        if (confirmationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TicketingSystemException("Token expired");
        }

        TicketingUser user = ticketingUserRepository.findByUserId(confirmationToken.getUserId()).orElseThrow(
                () -> new UserNotFoundException("User not found"));
        if (user.isAccountEnabled()) {
            throw new TicketingSystemException("Account already confirmed");
        }

        user.setAccountEnabled(true);

        tokenRepository.delete(confirmationToken);
        return true;
    }
}
