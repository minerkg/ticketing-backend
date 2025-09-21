package org.ubb.ticketing;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.ubb.ticketing.domain.customer.Customer;
import org.ubb.ticketing.domain.user.UserRole;
import org.ubb.ticketing.dto.TicketCreationRequest;
import org.ubb.ticketing.dto.user.UserRegistrationRequest;
import org.ubb.ticketing.repository.ComplaintTicketRepository;
import org.ubb.ticketing.repository.TicketingUserRepository;
import org.ubb.ticketing.service.ComplaintTicketService;
import org.ubb.ticketing.service.CustomerService;
import org.ubb.ticketing.service.type.SolutionTypeService;
import org.ubb.ticketing.service.type.TicketElementService;
import org.ubb.ticketing.service.user.TicketingUserService;

@Component
public class AddInitialData implements CommandLineRunner {

    private final ComplaintTicketService complaintTicketService;
    private final TicketingUserService ticketingUserService;

    private final Dotenv dotenv;
    private final TicketingUserRepository ticketingUserRepository;
    private final TicketElementService ticketElementService;
    private final SolutionTypeService solutionTypeService;
    private final CustomerService customerService;
    private final AuthenticationManager authenticationManager;
    private final ComplaintTicketRepository complaintTicketRepository;

    public AddInitialData(ComplaintTicketService complaintTicketService, TicketingUserService ticketingUserService, TicketingUserRepository ticketingUserRepository, TicketElementService ticketElementService, SolutionTypeService solutionTypeService, CustomerService customerService, AuthenticationManager authenticationManager, ComplaintTicketRepository complaintTicketRepository) {
        this.complaintTicketService = complaintTicketService;
        this.ticketingUserService = ticketingUserService;
        this.ticketElementService = ticketElementService;
        this.dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.ticketingUserRepository = ticketingUserRepository;
        this.solutionTypeService = solutionTypeService;
        this.customerService = customerService;
        this.authenticationManager = authenticationManager;
        this.complaintTicketRepository = complaintTicketRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        String usernameAdmin = System.getenv("USERNAME_ADMIN") != null
                ? System.getenv("USERNAME_ADMIN")
                : dotenv.get("USERNAME_ADMIN");
        String passwordAdmin = System.getenv("PASSWORD_ADMIN") != null
                ? System.getenv("PASSWORD_ADMIN")
                : dotenv.get("PASSWORD_ADMIN");
        UserRegistrationRequest adminUser = UserRegistrationRequest.builder()
                .username(usernameAdmin)
                .password(passwordAdmin)
                .firstName("System")
                .lastName("Administrator")
                .email("ors@ticketing.com")
                .build();


        complaintTicketRepository.deleteAll(complaintTicketRepository.findAll());

        ticketingUserRepository.findByUsername(usernameAdmin)
                .ifPresent(ticketingUserRepository::delete);


        var ticketingUserDto = ticketingUserService.registerUser(adminUser, "");
        var persistedUser = ticketingUserRepository.findByUsername(ticketingUserDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + ticketingUserDto.getUsername()));


        ticketingUserService.updateUserRole(usernameAdmin, UserRole.ADMIN);

        persistedUser.setAccountEnabled(true);
        ticketingUserRepository.save(persistedUser);


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usernameAdmin, passwordAdmin)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (ticketElementService.getAllTicketElements().isEmpty()) {
            ticketElementService.createTicketElement("Billing complaint");
            ticketElementService.createTicketElement("Service complaint");
            ticketElementService.createTicketElement("Other complaint");
        }
        if (solutionTypeService.getSolutionTypes().isEmpty()) {
            solutionTypeService.createSolutionType("Bill correction");
            solutionTypeService.createSolutionType("Compensation");
            solutionTypeService.createSolutionType("Information share");
        }

        if (customerService.findAll().isEmpty()) {
            Customer customer = Customer.builder()
                    .firstName("Andreea")
                    .lastName("Pop")
                    .email("csiszer_rs@yahoo.com")
                    .phoneNumber("0748882012")
                    .build();
            var savedCustomer = customerService.createCustomer(customer);
            complaintTicketService.createTicket(
                    TicketCreationRequest.builder()
                            .ticketElementName(ticketElementService.getAllTicketElements().stream().findFirst().get().getName())
                            .description("Test ticket")
                            .customerId(savedCustomer.getCustomerId())
                            .build(),
                    authentication);
        }


        ticketingUserService.updateUserRole(usernameAdmin, UserRole.ADMIN);



    }

}
