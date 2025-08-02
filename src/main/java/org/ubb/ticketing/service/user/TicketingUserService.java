package org.ubb.ticketing.service.user;


import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.ubb.ticketing.converter.UserDtoConverter;
import org.ubb.ticketing.domain.user.TicketingUser;
import org.ubb.ticketing.domain.user.UserRole;
import org.ubb.ticketing.domain.validator.PasswordValidator;
import org.ubb.ticketing.domain.validator.TicketingUserValidator;
import org.ubb.ticketing.dto.TicketingUserDto;
import org.ubb.ticketing.dto.UserRegistrationRequest;
import org.ubb.ticketing.exception.PasswordException;
import org.ubb.ticketing.exception.UserAlreadyExistsException;
import org.ubb.ticketing.exception.UserNotFoundException;
import org.ubb.ticketing.repository.TicketingUserRepository;

import java.util.List;

@Service
public class TicketingUserService {


    private final TicketingUserRepository ticketingUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final TicketingUserValidator ticketingUserValidator;
    private final PasswordValidator passwordValidator;
    private final Logger logger = LoggerFactory.getLogger(TicketingUserService.class);
    private final UserDtoConverter userDtoConverter;

    public TicketingUserService(TicketingUserRepository ticketingUserRepository, PasswordEncoder passwordEncoder, TicketingUserValidator ticketingUserValidator, PasswordValidator passwordValidator, UserDtoConverter userDtoConverter) {
        this.ticketingUserRepository = ticketingUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.ticketingUserValidator = ticketingUserValidator;
        this.passwordValidator = passwordValidator;
        this.userDtoConverter = userDtoConverter;
    }


    @Transactional
    public TicketingUserDto registerUser(UserRegistrationRequest userRequest) {
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
                    .build();

            // TODO: Validate that the email exists and is confirmed

            return userDtoConverter.convertModelToDto(ticketingUserRepository.save(newUser));

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


    @PreAuthorize("hasRole('ADMIN')")
    public List<TicketingUserDto> getAllUsers() {
        return ticketingUserRepository.findAllUsersWithoutPassword();
    }

    public TicketingUserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        TicketingUser currentUser = ticketingUserRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + userDetails.getUsername()));
        return userDtoConverter.convertModelToDto(currentUser);
    }
}
