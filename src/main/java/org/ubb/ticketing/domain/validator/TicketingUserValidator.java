package org.ubb.ticketing.domain.validator;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.ubb.ticketing.dto.user.UserRegistrationRequest;
import org.ubb.ticketing.repository.TicketingUserRepository;

@Component
public class TicketingUserValidator implements Validator {

    private final TicketingUserRepository ticketingUserRepository;


    public TicketingUserValidator(TicketingUserRepository ticketingUserRepository) {
        this.ticketingUserRepository = ticketingUserRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRegistrationRequest request = (UserRegistrationRequest) target;

        if (ticketingUserRepository.findByEmail(request.getEmail()).isPresent()) {
            errors.rejectValue("email", "duplicate", "Email is already in use");
        }

        if (ticketingUserRepository.findByUsername(request.getUsername()).isPresent()) {
            errors.rejectValue("username", "duplicate", "Username is already taken");
        }
    }
}

