package org.ubb.ticketing.domain.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.ubb.ticketing.dto.UserDetailUpdateRequest;
import org.ubb.ticketing.dto.UserRegistrationRequest;
import org.ubb.ticketing.repository.TicketingUserRepository;

@Component
public class TicketingUserDetailUpdateValidator implements Validator {

    private final TicketingUserRepository ticketingUserRepository;


    public TicketingUserDetailUpdateValidator(TicketingUserRepository ticketingUserRepository) {
        this.ticketingUserRepository = ticketingUserRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDetailUpdateRequest request = (UserDetailUpdateRequest) target;

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "empty", "Email must not be empty");
        }
        else if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errors.rejectValue("email", "invalid", "Email format is invalid");
        }
        else {
            ticketingUserRepository.findByEmail(request.getEmail())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getUserId().equals(request.getUserId())) {
                            errors.rejectValue("email", "duplicate", "Email is already in use by another user");
                        }
                    });
        }

        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            errors.rejectValue("firstName", "empty", "First name must not be empty");
        }

        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            errors.rejectValue("lastName", "empty", "Last name must not be empty");
        }

    }


}
