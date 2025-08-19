package org.ubb.ticketing;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.ubb.ticketing.domain.client.Client;
import org.ubb.ticketing.domain.user.UserRole;
import org.ubb.ticketing.dto.TicketCreationRequest;
import org.ubb.ticketing.dto.UserRegistrationRequest;
import org.ubb.ticketing.repository.TicketingUserRepository;
import org.ubb.ticketing.service.ComplaintTicketService;
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

    public AddInitialData(ComplaintTicketService complaintTicketService, TicketingUserService ticketingUserService, TicketingUserRepository ticketingUserRepository, TicketElementService ticketElementService, SolutionTypeService solutionTypeService) {
        this.complaintTicketService = complaintTicketService;
        this.ticketingUserService = ticketingUserService;
        this.ticketElementService = ticketElementService;
        this.dotenv = Dotenv.load();
        this.ticketingUserRepository = ticketingUserRepository;
        this.solutionTypeService = solutionTypeService;
    }


    @Override
    public void run(String... args) throws Exception {


        String usernameAdmin = dotenv.get("USERNAME_ADMIN");
        String passwordAdmin = dotenv.get("PASSWORD_ADMIN");
        UserRegistrationRequest adminUser = UserRegistrationRequest.builder()
                .username(usernameAdmin)
                .password(passwordAdmin)
                .firstName("System")
                .lastName("Administrator")
                .email("ors@ticketing.com")
                .build();

        var ticketingUserDto = ticketingUserService.registerUser(adminUser);
        var createdUser = ticketingUserRepository.findByUsername(ticketingUserDto.getUsername()).orElseThrow(
                () -> new RuntimeException("User not found: " + ticketingUserDto.getUsername())
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                createdUser,
                createdUser.getPassword(),
                createdUser.getAuthorities()
        );


        ticketElementService.createTicketElement("Billing complaint");
        ticketElementService.createTicketElement("Service complaint");
        ticketElementService.createTicketElement("Other complaint");

        solutionTypeService.createSolutionType("Bill correction");
        solutionTypeService.createSolutionType("Compensation");
        solutionTypeService.createSolutionType("Information share");

        Client client = Client.builder()
                .firstName("Andreea")
                .lastName("Pop")
                .email("csiszer_rs@yahoo.com")
                .phoneNumber("0748882012")
                .build();

        complaintTicketService.createTicket(
                TicketCreationRequest.builder()
                        .ticketElementName(ticketElementService.getAllTicketElements().stream().findFirst().get().getName())
                        .description("Test ticket")
                        .client(client)
                        .build(),
                authentication);


        ticketingUserService.updateUserRole(ticketingUserDto.getUsername(), UserRole.ADMIN);
    }

}
