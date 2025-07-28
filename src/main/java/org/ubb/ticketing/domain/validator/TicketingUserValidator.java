package org.ubb.ticketing.domain.validator;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.ubb.ticketing.dto.UserRegistrationRequest;
import org.ubb.ticketing.repository.UserRepository;

@Component
public class TicketingUserValidator implements Validator {

    private final UserRepository userRepository;


    public TicketingUserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRegistrationRequest request = (UserRegistrationRequest) target;

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            errors.rejectValue("email", "duplicate", "Email is already in use");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            errors.rejectValue("username", "duplicate", "Username is already taken");
        }
    }
}

