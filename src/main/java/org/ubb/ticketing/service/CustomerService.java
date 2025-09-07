package org.ubb.ticketing.service;

import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.ubb.ticketing.domain.customer.Customer;
import org.ubb.ticketing.domain.validator.CustomerValidator;
import org.ubb.ticketing.exception.CustomerNotFoundException;
import org.ubb.ticketing.repository.CustomerRepository;
import org.ubb.ticketing.service.notification.NotificationService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class CustomerService {


    private final CustomerRepository customerRepository;
    private final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private final NotificationService notificationService;
    private final CustomerValidator customerValidator;

    public CustomerService(CustomerRepository customerRepository, NotificationService notificationService, CustomerValidator customerValidator) {
        this.customerRepository = customerRepository;
        this.notificationService = notificationService;
        this.customerValidator = customerValidator;
    }


    public Set<Customer> findAll() {
        logger.debug("Customers found is successful");
        return new HashSet<>(customerRepository.findAll());
    }

    public Customer findById(UUID id) {
        logger.debug("Customer found is successful");
        return customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Customer with the given id is not found!")
        );
    }

    public Customer createCustomer(Customer customer) {
        Errors customerErrors = new BeanPropertyBindingResult(customer, "customer");
        customerValidator.validate(customer, customerErrors);

        if (customerErrors.hasErrors()) {
            throw new ValidationException("Customer registration validation failed: " + customerErrors.getAllErrors());
        }

        //TODO: send e-mail to teh customer

        logger.debug("Customer created is successful");
        return this.customerRepository.save(customer);

    }


}
