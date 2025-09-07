package org.ubb.ticketing.domain.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.ubb.ticketing.domain.customer.Customer;
import org.ubb.ticketing.repository.CustomerRepository;

@Component
public class CustomerValidator implements Validator {

    private final CustomerRepository customerRepository;


    public CustomerValidator(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Customer.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Customer request = (Customer) target;

        if (customerRepository.findByEmail((request.getEmail())).isPresent()) {
            errors.rejectValue("email", "duplicate", "Email is already in use");
        }

        if (customerRepository.findByPhoneNumber((request.getPhoneNumber())).isPresent()) {
            errors.rejectValue("phoneNumber", "duplicate", "Phone number is already taken");
        }
    }
}
