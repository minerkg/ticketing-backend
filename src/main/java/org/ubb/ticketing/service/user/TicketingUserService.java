package org.ubb.ticketing.service.user;


import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.ubb.ticketing.domain.user.TicketingUser;
import org.ubb.ticketing.domain.user.UserRole;
import org.ubb.ticketing.domain.validator.TicketingUserValidator;
import org.ubb.ticketing.dto.UserRegistrationRequest;
import org.ubb.ticketing.exception.UserAlreadyExistsException;
import org.ubb.ticketing.repository.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class TicketingUserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TicketingUserValidator ticketingUserValidator;
    //private final UserDtoConverter userDtoConverter;

    public TicketingUserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TicketingUserValidator ticketingUserValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.ticketingUserValidator = ticketingUserValidator;
    }


    @Transactional
    public TicketingUser registerUser(UserRegistrationRequest userRequest) {

        Errors errors = new BeanPropertyBindingResult(userRequest, "userRequest");
        ticketingUserValidator.validate(userRequest, errors);

        if (errors.hasErrors()) {
            throw new ValidationException("User registration validation failed: " + errors.getAllErrors());
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

            return userRepository.save(newUser);

        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("A user with the provided email or username already exists", e);
        }
    }



    @Transactional
    public void updateUserRole(String username, UserRole newRole) throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach(a -> System.out.println("Authority: " + a.getAuthority()));
        if (authentication == null || !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only admins can update user roles.");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        user.setRole(newRole);
    }

    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public User updateUser(UpdateUserDto updateData) {
        User user = userRepository.findByUsername(updateData.getUsername()).orElseThrow(() -> new UserNotFoundException("User not found with username: " + updateData.getUsername()));
        user.setFirst_name(updateData.getFirst_name());
        user.setLast_name(updateData.getLast_name());
        user.setEmail(updateData.getEmail());
        user.setPhone(updateData.getPhone());

        return user;
    }


    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

}
