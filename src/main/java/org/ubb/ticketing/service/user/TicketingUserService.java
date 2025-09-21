package org.ubb.ticketing.service.user;


import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.ubb.ticketing.converter.UserDtoConverter;
import org.ubb.ticketing.domain.user.ConfirmationToken;
import org.ubb.ticketing.domain.user.TicketingUser;
import org.ubb.ticketing.domain.user.UserRole;
import org.ubb.ticketing.domain.validator.PasswordValidator;
import org.ubb.ticketing.domain.validator.TicketingUserDetailUpdateValidator;
import org.ubb.ticketing.domain.validator.TicketingUserValidator;
import org.ubb.ticketing.dto.user.*;
import org.ubb.ticketing.exception.PasswordException;
import org.ubb.ticketing.exception.TicketingSystemException;
import org.ubb.ticketing.exception.UserAlreadyExistsException;
import org.ubb.ticketing.exception.UserNotFoundException;
import org.ubb.ticketing.repository.TicketingUserRepository;
import org.ubb.ticketing.repository.TokenRepository;
import org.ubb.ticketing.service.notification.NotificationService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.UUID;


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
    private final AuthenticationManager authenticationManager;

    public TicketingUserService(TicketingUserRepository ticketingUserRepository, PasswordEncoder passwordEncoder, TicketingUserValidator ticketingUserValidator, TicketingUserDetailUpdateValidator ticketingUserUpdateValidator, PasswordValidator passwordValidator, UserDtoConverter userDtoConverter, TokenRepository tokenRepository, ConfirmationTokenService confirmationTokenService, NotificationService notificationService, JwtService jwtService, AuthenticationManager authenticationManager) {
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
        this.authenticationManager = authenticationManager;
    }


    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("USER");

        String accessToken = jwtService.generateAccessToken(username, role);

        TicketingUserDto user = this.getCurrentUserDto(authentication);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .user(user)
                .build();
    }


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

            notificationService.notifyTokenGenerated(savedNewUser, token, baseUrl, "register");

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


    public void changePassword(String username, String currentPassword, String newPassword, String baseUrl) {
        TicketingUser user = ticketingUserRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        if (!user.isAccountEnabled()) {
            throw new TicketingSystemException("Account is not enabled, if you want to change the password, " +
                    "please confirm your account first or contact an administrator");
        } else {
            //block account
            user.setAccountEnabled(false);
        }


        Map<String, Object> target = new HashMap<>();
        target.put("password", newPassword);

        Errors errors = new MapBindingResult(target, "password");
        passwordValidator.validate(newPassword, errors);


        passwordValidator.validate(newPassword, errors);
        if (errors.hasErrors()) {
            throw new PasswordException("Password does not meet security requirements.");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PasswordException("Current password is incorrect");
        }
        var encodedPassword = passwordEncoder.encode(newPassword);

        // sending confirmation mail
        var token = confirmationTokenService.generateToken(user.getUserId(), encodedPassword);
        tokenRepository.save(token);

        notificationService.notifyTokenGenerated(user, token, baseUrl, "reset");

        logger.debug("Password changed for user {}", username);
        //on confirmation, enable account using confirmPasswordChange method and effectively change password
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public List<TicketingUserDto> getAllUsers() {
        return ticketingUserRepository.findAllUsersWithoutPassword();
    }

    public TicketingUserDto getCurrentUserDto(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username;
        switch (principal) {
            case org.springframework.security.oauth2.jwt.Jwt jwt -> {
                // request with JWT token
                username = jwt.getClaim("sub");
            }
            case org.springframework.security.core.userdetails.UserDetails userDetails -> {
                // programmatic login
                username = userDetails.getUsername();
            }
            case String str -> {
                // principal is just a username
                username = str;
            }
            default -> {
                throw new IllegalStateException("Unsupported principal type: " + principal.getClass());
            }
        }

        TicketingUser currentUser = ticketingUserRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        return userDtoConverter.convertModelToDto(currentUser);
    }

    public TicketingUser getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username;
        switch (principal) {
            case org.springframework.security.oauth2.jwt.Jwt jwt -> {
                // request with JWT token
                username = jwt.getClaim("sub");
            }
            case org.springframework.security.core.userdetails.UserDetails userDetails -> {
                // programmatic login
                username = userDetails.getUsername();
            }
            case String str -> {
                // principal is just a username
                username = str;
            }
            default -> {
                throw new IllegalStateException("Unsupported principal type: " + principal.getClass());
            }
        }
        return ticketingUserRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }


    @Transactional
    public TicketingUserDto updateUserDetails(Authentication authentication, UserDetailUpdateRequest userRequest) {
        logger.info("Updating user details for user {}", userRequest.getUsername());
        Errors userErrors = new BeanPropertyBindingResult(userRequest, "userRequest");

        var currentUser = this.getCurrentUser(authentication);


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


    @Transactional
    public boolean confirmPasswordChange(String token) {
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

        user.setPassword(confirmationToken.getEncodedPassword());
        user.setAccountEnabled(true);
        tokenRepository.delete(confirmationToken);
        ticketingUserRepository.save(user);
        logger.info("Password for user {} changed successfully", user.getUsername());
        return true;
    }


    public RefreshResponse refreshToken(String refreshToken) {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new AccessDeniedException("not authorized");
        }
        String username = jwtService.extractUsername(refreshToken);
        String role = jwtService.extractRole(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(username, role);

        return RefreshResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void enableAccount(UUID userId) {
        logger.info("Enabling account for user {}", userId);
        TicketingUser user = ticketingUserRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        if (user.isAccountEnabled()) {
            throw new TicketingSystemException("Account already enabled");
        }
        user.setAccountEnabled(true);
        ticketingUserRepository.save(user);
        logger.info("Account for user {} enabled successfully", user.getUsername());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void disableAccount(UUID userId) {
        logger.info("Disabling account for user {}", userId);
        TicketingUser user = ticketingUserRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        if (!user.isAccountEnabled()) {
            throw new TicketingSystemException("Account already disabled");
        }
        user.setAccountEnabled(false);
        ticketingUserRepository.save(user);
        logger.info("Account for user {} disabled successfully", user.getUsername());
    }


}
