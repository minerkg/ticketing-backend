package org.ubb.ticketing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.ubb.ticketing.domain.customer.Customer;
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

    public CustomerService(CustomerRepository customerRepository, NotificationService notificationService) {
        this.customerRepository = customerRepository;
        this.notificationService = notificationService;
    }


    public Set<Customer> findAll() {
        return new HashSet<>(customerRepository.findAll());
    }

    public Customer findById(UUID id) {
        return customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Customer with the given id is not found!")
        );
    }

    public Customer createCustomer(Customer customer) {
        return this.customerRepository.save(customer);
    }


}
