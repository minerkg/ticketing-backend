package org.ubb.ticketing.controller;


import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.ubb.ticketing.domain.customer.Customer;
import org.ubb.ticketing.dto.CustomerDto;
import org.ubb.ticketing.dto.CustomerRequest;
import org.ubb.ticketing.exception.TicketingSystemException;
import org.ubb.ticketing.service.CustomerService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class CustomerController {


    private final CustomerService customerService;
    private final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final ModelMapper modelMapper;

    public CustomerController(CustomerService customerService, ModelMapper modelMapper) {
        this.customerService = customerService;
        this.modelMapper = modelMapper;
    }


    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerDto>> getCustomerById(@PathVariable UUID customerId) {
        logger.debug("getCustomerById method accessed");
        try {
            var customer = customerService.findById(customerId);
            logger.debug("customer found by id is successful");
            return ResponseEntity.ok(new ApiResponse<>(
                    "customer created",
                    modelMapper.map(customer, CustomerDto.class)));
        } catch (TicketingSystemException e) {
            logger.error("getCustomerById internal error", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(e.getMessage(), null));
        } catch (AuthenticationException e) {
            logger.error("getCustomerById no authentication", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("no authentication", null));
        } catch (Exception e) {
            logger.error("getCustomerById internal server error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<Set<CustomerDto>>> getAllCustomers() {
        logger.debug("getAllCustomers method accessed");
        try {
            var customerList = customerService.findAll();
            var customerDtoList = customerList.stream()
                    .map(customer -> modelMapper.map(customer, CustomerDto.class)).toList();
            logger.debug("customers found is successful");
            return ResponseEntity.ok(new ApiResponse<>("customers found", new HashSet<>(customerDtoList)));
        } catch (TicketingSystemException e) {
            logger.error("getAllCustomers internal error", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(e.getMessage(), null));
        } catch (AuthenticationException e) {
            logger.error("getAllCustomers no authentication", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("no authentication", null));
        } catch (Exception e) {
            logger.error("getAllCustomers internal server error", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping("")
    public ResponseEntity<ApiResponse<CustomerDto>> addCustomer(@RequestBody CustomerRequest customerRequest) {
        logger.debug("addCustomer method accessed");
        try {
            var customer = modelMapper.map(customerRequest, Customer.class);
            var savedCustomer = customerService.createCustomer(customer);
            logger.debug("customer created successfully");
            return ResponseEntity.ok(new ApiResponse<>(
                    "customer created",
                    modelMapper.map(savedCustomer, CustomerDto.class)));
        } catch (TicketingSystemException e) {
            logger.error("create new customer internal error", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(e.getMessage(), null));
        } catch (AuthenticationException e) {
            logger.error("create new customer no authentication", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("no authentication", null));
        } catch (Exception e) {
            logger.error("create new customer internal server error", e);
            return ResponseEntity.internalServerError().build();
        }

    }
}
